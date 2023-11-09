package com.pur.formplugin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import com.alibaba.dubbo.common.utils.StringUtils;
import kd.bos.algo.DataSet;
import kd.bos.algo.JoinDataSet;
import kd.bos.algo.Row;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.metadata.clr.DataEntityPropertyCollection;
import kd.bos.dataentity.metadata.dynamicobject.DynamicObjectType;
import kd.bos.entity.report.AbstractReportListDataPlugin;
import kd.bos.entity.report.FilterInfo;
import kd.bos.entity.report.ReportQueryParam;
import kd.bos.orm.ORM;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import org.antlr.v4.unicode.UnicodeData;

public class PurcontractmentRepQuery extends AbstractReportListDataPlugin {
    @Override
    public DataSet query(ReportQueryParam queryParam, Object arg1) throws Throwable {
        FilterInfo filterInfo = queryParam.getFilter();
        String contractnosf = filterInfo.getString("tpv_contractnumber");//申请单号
        String status = filterInfo.getString("tpv_billstatus1");//单据状态
//        String tpvBillstatus2 = filterInfo.getString("tpv_billstatus2");//来源单据类型
        List<QFilter> list = new ArrayList<>();
        if (!StringUtils.isEmpty(status)){
            list.add(new QFilter("billstatus", QCP.equals, status));

        }
        if (!StringUtils.isEmpty(contractnosf)) {
            list.add(  new QFilter("billno", QCP.like, "%"+contractnosf+"%"));
        }
//        if(!StringUtils.isEmpty(tpvBillstatus2)) {
//            list.add(new QFilter("tpv_billstatus2",QCP.equals,tpvBillstatus2));
//        }

        DataSet purcontract = this.queryWork(list);
        return purcontract;
    }

    private DataSet queryWork(List<QFilter> list) {
        String selectFiles =
                        "to_char(id) tpv_texted," //数据编号
                        +"billno tpv_billno," //申请单号
                        + "tpv_applyorg tpv_applyorg,"
                        + "'采购' tpv_textfield,"
                        + "tpv_applier tpv_applier,"
                        + "tpv_applydate tpv_applydate,"
                        + "billstatus tpv_billstatus2,"
                        + "tpv_gjtt_purchase_cg.tpv_materia tpv_materia,"
                        + "tpv_gjtt_purchase_cg.tpv_unit tpv_unit,"
                        + "tpv_gjtt_purchase_cg.tpv_price tpv_price,"
                        + "tpv_gjtt_purchase_cg.tpv_applyqty tpv_applyqty,"
                        + "tpv_gjtt_purchase_cg.tpv_orderedqty tpv_orderedqty,"
                        + "tpv_gjtt_purchase_cg.tpv_amount tpv_amount";

        DataSet queryDataSet = ORM.create().queryDataSet(this.getClass().getName(), "tpv_gjtt_purchase", selectFiles,
                list.toArray(new QFilter[0]));
        String selectFiles1 =
                        "to_char(id) tpv_texted," //数据编号
                        +"billno tpv_billno," //申请单号
                        + "tpv_applyorg tpv_applyorg,"
                        + "'销售' tpv_textfield,"
                        + "tpv_applier tpv_applier,"
                        + "tpv_applydate tpv_applydate,"
                        + "billstatus tpv_billstatus2,"
                        + "tpv_gjtt_sale_cg.tpv_materia tpv_materia,"
                        + "tpv_gjtt_sale_cg.tpv_unit tpv_unit,"
                        + "tpv_gjtt_sale_cg.tpv_price tpv_price,"
                        + "tpv_gjtt_sale_cg.tpv_applyqty tpv_applyqty,"
                        + "tpv_gjtt_sale_cg.tpv_orderedqty tpv_orderedqty,"
                        + "tpv_gjtt_sale_cg.tpv_amount tpv_amount";

        DataSet queryDataSet1 = ORM.create().queryDataSet(this.getClass().getName(), "tpv_gjtt_sale", selectFiles1,
                list.toArray(new QFilter[0]));
        DataSet union = queryDataSet.union(queryDataSet1);

       String selectFile2 =
                        "tpv_gjtt_purchaseord_x.tpv_price tpv_price,"
                       + "tpv_gjtt_purchaseord_x.tpv_applyqty tpv_decimalfield,"
                       + "tpv_org tpv_applyorg1,"
                       + "tpv_gjtt_purchaseord_x.tpv_amount tpv_amount,"
                       +" to_char(tpv_bigintfield) tpv_texted";//数据编号

       DataSet queryDataSet2 = ORM.create().queryDataSet(this.getClass().getName(), "tpv_gjtt_purchaseord", selectFile2,
               list.toArray(new QFilter[0]));

        DataSet finish = union.leftJoin(queryDataSet2).on("tpv_texted", "tpv_texted").select(new String[]{
                "tpv_billno", "tpv_applyorg", "tpv_applier", "tpv_applydate", "tpv_materia", "tpv_unit", "tpv_price", "tpv_textfield", "tpv_applyqty", "tpv_orderedqty", "tpv_billstatus2", "tpv_amount","tpv_applyorg1","tpv_decimalfield"
        }).finish();


        return finish.distinct();




    }



}

