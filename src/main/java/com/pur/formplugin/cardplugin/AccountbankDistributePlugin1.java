package com.pur.formplugin.cardplugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kd.bos.algo.DataSet;
import kd.bos.algo.JoinType;
import kd.bos.algo.Row;
import kd.bos.context.RequestContext;
import kd.bos.dataentity.resource.ResManager;
import kd.bos.entity.datamodel.events.PropertyChangedArgs;
import kd.bos.form.chart.Axis;
import kd.bos.form.chart.AxisType;
import kd.bos.form.chart.BarSeries;
import kd.bos.form.chart.HistogramChart;
import kd.bos.form.chart.Label;
import kd.bos.form.chart.Position;
import kd.bos.form.chart.XAlign;
import kd.bos.form.chart.YAlign;
import kd.bos.form.container.Tab;
import kd.bos.form.control.events.TabSelectEvent;
import kd.bos.form.control.events.TabSelectListener;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QFilter;
import kd.bos.orm.util.CollectionUtils;
import kd.bos.servicehelper.QueryServiceHelper;
import kd.tmc.fbp.common.helper.TmcOrgDataHelper;
import kd.tmc.fbp.common.util.EmptyUtil;

public class AccountbankDistributePlugin1 extends AbstractFormPlugin {
    //金融机构
    private static final String BTN_FINORG = "tpv_btn_finorg";
    //账户用途
    private static final String BTN_ACCTPURPOSE = "tpv_btn_acctpurpose";
    //区域
    private static final String BTN_LOCAL = "tpv_btn_local";
    //币别
    private static final String BTN_CURRENCY = "tpv_btn_currency";
    //页签控件
    private static final String TABAP = "tpv_tabap";
    private static final String CACHEKEY_DIMENSION = "dimension";
    private HistogramChart customChart = null;
    private List<String> names = new ArrayList();
    private List<Integer> values = new ArrayList();

    public AccountbankDistributePlugin1() {
    }

    public void registerListener(EventObject e) {
        super.registerListener(e);
        Tab tab = (Tab) this.getControl("tpv_tabap");
        tab.addTabSelectListener(new TabSelectListener() {
            public void tabSelected(TabSelectEvent event) {
                AccountbankDistributePlugin1.this.setDimension(event.getTabKey());
                AccountbankDistributePlugin1.this.fetchData();
                AccountbankDistributePlugin1.this.paintChart(AccountbankDistributePlugin1.this.names, AccountbankDistributePlugin1.this.values);
            }
        });
    }

    private String getDimension() {
        String dimension = this.getPageCache().get("dimension");
        if (EmptyUtil.isEmpty(dimension)) {
            dimension = "tpv_btn_finorg";
        }

        return dimension;
    }

    private void setDimension(String key) {
        this.getPageCache().put("dimension", key);
    }

    private String getShowmode() {
        String showmode = this.getPageCache().get("tpv_showmode");
        if (EmptyUtil.isEmpty(showmode)) {
            showmode = "10";
        }

        return showmode;
    }

    private void setShowmode(String value) {
        this.getPageCache().put("tpv_showmode", value);
    }

    //设置前几
    public void propertyChanged(PropertyChangedArgs e) {
        super.propertyChanged(e);
        String key = e.getProperty().getName();
        if ("tpv_showmode".equals(key)) {
            String paymode = (String) this.getModel().getValue("tpv_showmode");
            paymode = EmptyUtil.isEmpty(paymode) ? "10" : paymode;
            this.setShowmode(paymode);
            this.fetchData();
            this.paintChart(this.names, this.values);
        }

    }

    public void afterCreateNewData(EventObject e) {
        super.afterCreateNewData(e);
        this.fetchData();
        this.paintChart(this.names, this.values);
    }

    private void fetchData() {
        String appId = this.getView().getFormShowParameter().getAppId();
        RequestContext context = RequestContext.get();
        //账户查询表单
        List<Long> comIdList = TmcOrgDataHelper.getAuthorizedBankOrgId(Long.valueOf(context.getUserId()), appId, "am_accountbank", "47150e89000000ac");
        List<QFilter> accountbankQFilters = new ArrayList();
        accountbankQFilters.add(new QFilter("company.id", "in", comIdList));
        List<Object> statusList = new ArrayList();
        statusList.add("normal");
        statusList.add("closing");
        accountbankQFilters.add(new QFilter("acctstatus", "in", statusList));
        DataSet bizDs = null;
        String dimension = this.getDimension();
        String groupByFieldStr = "bank.bank_cate.name";
        switch (dimension) {
            case "tpv_btn_finorg":
                groupByFieldStr = "bank_cate";
                bizDs = this.queryData_ByFinOrgInfo((QFilter[]) accountbankQFilters.toArray(new QFilter[0]));
                break;
            case "tpv_btn_acctpurpose":
                groupByFieldStr = "acctproperty";
                bizDs = this.queryData_ByAcctPurpose((QFilter[]) accountbankQFilters.toArray(new QFilter[0]));
                break;
            case "tpv_btn_local":
                groupByFieldStr = "city";
                bizDs = this.queryData_ByLocal((QFilter[]) accountbankQFilters.toArray(new QFilter[0]));
                break;
            case "tpv_btn_currency":
                groupByFieldStr = "currencyname";
                bizDs = this.queryData_ByCurrency((QFilter[]) accountbankQFilters.toArray(new QFilter[0]));
        }

        String paymode = this.getShowmode();
        if (bizDs != null) {
            bizDs = bizDs.orderBy(new String[]{"count desc"}).top(Integer.parseInt(paymode));
            Iterator<Row> it = bizDs.iterator();
            if (!it.hasNext()) {
                this.getView().setVisible(true, new String[]{"tpv_flex_quesheng"});
                this.getView().setVisible(false, new String[]{"tpv_flexpanelap11"});
                return;
            }

            this.getView().setVisible(false, new String[]{"tpv_flex_quesheng"});
            ArrayList xName = new ArrayList();
            List<Integer> yValue = new ArrayList();

            while (it.hasNext()) {
                Row row = (Row) it.next();
                xName.add(row.getString(groupByFieldStr));
                yValue.add(row.getInteger("count") == null ? Integer.getInteger("0") : row.getInteger("count"));
            }

            this.names.clear();
            this.values.clear();
            this.names.addAll(xName);
            this.values.addAll(yValue);
        }

    }

    protected DataSet queryData_ByCurrency(QFilter[] filter) {
        String bankAcctSic = "currency,currency.fbasedataid";
        DataSet bankAcctSet = QueryServiceHelper.queryDataSet("BankAcctSum", "bd_accountbanks", bankAcctSic, filter, (String) null);
        String currency = "id,name currencyname";
        DataSet currencySet = QueryServiceHelper.queryDataSet("BankAcctSum", "bd_currency", currency, (QFilter[]) null, (String) null);
        bankAcctSet = bankAcctSet.join(currencySet, JoinType.LEFT).on("currency.fbasedataid", "id").select(new String[]{"currencyname"}).finish();
        return bankAcctSet.groupBy(new String[]{"currencyname"}).count().finish();
    }

    protected DataSet queryData_ByFinOrgInfo(QFilter[] filter) {
        String bankAcctSic = "case when finorgtype='0' then bank.bank_cate.name else bank.name end as bank_cate";
        DataSet bankAcctSet = QueryServiceHelper.queryDataSet("BankAcctSum", "bd_accountbanks", bankAcctSic, filter, (String) null);
        return bankAcctSet.groupBy(new String[]{"bank_cate"}).count().finish();
    }

    protected DataSet queryData_ByLocal(QFilter[] filter) {
        String bankAcctSic = "bank.id";
        DataSet bankAcctSet = QueryServiceHelper.queryDataSet("BankAcctSum", "bd_accountbanks", bankAcctSic, filter, (String) null);
        String finOrgSic = "id,city.name city";
        DataSet bankSet = QueryServiceHelper.queryDataSet("BankAcctSum", "bd_finorginfo", finOrgSic, (QFilter[]) null, (String) null);
        bankAcctSet = bankAcctSet.join(bankSet, JoinType.LEFT).on("bank.id", "id").select(new String[]{"city"}).finish();
        return bankAcctSet.groupBy(new String[]{"city"}).count().finish();
    }

    protected DataSet queryData_ByAcctPurpose(QFilter[] filter) {
        String bankAcctSic = "acctproperty.name acctproperty";
        DataSet bankAcctSet = QueryServiceHelper.queryDataSet("BankAcctSum", "bd_accountbanks", bankAcctSic, filter, (String) null);
        return bankAcctSet.groupBy(new String[]{"acctproperty"}).count().finish();
    }

    /*
     * 柱状图x轴名称
     * y轴数值
     *
     * */
    private void paintChart(List<String> xName, List<Integer> yValue) {
        this.customChart = (HistogramChart) this.getControl("tpv_expchart");
        if (null != this.customChart && !CollectionUtils.isEmpty(this.values)) {
            this.customChart.clearData();
            this.customChart.setDraggable(true);
            this.customChart.setShowTooltip(true);
            this.customChart.setTitleAlign(XAlign.left, YAlign.top);
            this.customChart.setMargin(Position.left, "30px");
            this.customChart.setMargin(Position.right, "10px");
            BarSeries barSeries = this.customChart.createBarSeries(ResManager.loadKDString("账户数", "AccountbankDistributePlugin_0", "tmc-am-formplugin", new Object[0]));
            Collections.replaceAll(xName, null, ResManager.loadKDString("未命名", "AccountbankDistributePlugin_7", "tmc-am-formplugin", new Object[0]));
            Collections.replaceAll(xName, "", ResManager.loadKDString("未命名", "AccountbankDistributePlugin_7", "tmc-am-formplugin", new Object[0]));
            if (xName.size() < 5) {
                barSeries.setBarWidth("100");
            } else if (xName.size() < 9) {
                barSeries.setBarWidth("50");
            } else if (xName.size() < 11) {
                barSeries.setBarWidth("45");
            } else {
                barSeries.setBarWidth("4");
            }

            Axis xAxis = this.customChart.createXAxis(ResManager.loadKDString("银行账户", "AccountbankDistributePlugin_1", "tmc-am-formplugin", new Object[0]), AxisType.category);
            Map<String, String> axisLabel = new HashMap();
            xAxis.setPropValue("axisLabel", axisLabel);
            xAxis.setCategorys(xName);
            Axis yAxis = this.customChart.createYAxis(ResManager.loadKDString("账户数 ", "AccountbankDistributePlugin_2", "tmc-am-formplugin", new Object[0]), AxisType.value);
            barSeries.setColor("#5F8AFF");
            Iterator var7 = yValue.iterator();

            while (var7.hasNext()) {
                Integer amount = (Integer) var7.next();
                barSeries.addData(amount);
            }

            this.setLableStyle(barSeries);
            this.setLinearGradient(barSeries, "#5E80EB", "#83bff6");
            this.setLineColor(xAxis, "#666666");
            this.setLineColor(yAxis, "#666666");
            this.customChart.refresh();
        } else {
            this.getView().setVisible(false, new String[]{"expchart"});
        }
    }

    private void setLableStyle(BarSeries barSeries) {
        Label label = new Label();
        label.setShow(true);
        label.setPosition(Position.top);
        label.setColor("#5F8AFF");
        label.setFormatter("function(itemValue,index){var itemData = itemValue.value;return itemData;}");
        barSeries.setLabel(label);
        List<Object> funPath = new ArrayList();
        funPath.add("label");
        funPath.add("normal");
        funPath.add("formatter");
        barSeries.addFuncPath(funPath);
    }

    private void setLinearGradient(BarSeries barSeries, String color1, String color2) {
        List<Object> funPath = new ArrayList();
        funPath.add("itemStyle");
        funPath.add("normal");
        funPath.add("color");
        barSeries.addFuncPath(funPath);
        HashMap<String, Object> map = new HashMap();
        HashMap<String, Object> normap = new HashMap();
        map.put("color", "new echarts.graphic.LinearGradient(0, 1, 0, 0, [{\"offset\": 0,\"color\": '" + color1 + "'}, {\"offset\": 1, \"color\": '" + color2 + "'}])");
        normap.put("normal", map);
        barSeries.setPropValue("itemStyle", normap);
    }

    private void setLineColor(Axis axix, String color) {
        Map<String, Object> axisLineMap = new HashMap();
        Map<String, Object> lineStyleMap = new HashMap();
        lineStyleMap.put("color", color);
        axisLineMap.put("lineStyle", lineStyleMap);
        axix.setPropValue("axisLine", axisLineMap);
    }
}
