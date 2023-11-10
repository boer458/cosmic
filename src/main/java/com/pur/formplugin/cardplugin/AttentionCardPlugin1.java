package com.pur.formplugin.cardplugin;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.resource.ResManager;
import kd.bos.entity.MainEntityType;
import kd.bos.entity.datamodel.events.ChangeData;
import kd.bos.entity.datamodel.events.PropertyChangedArgs;
import kd.bos.entity.format.FormatFactory;
import kd.bos.entity.format.FormatObject;
import kd.bos.entity.format.FormatTypes;
import kd.bos.entity.operate.result.OperateErrorInfo;
import kd.bos.entity.operate.result.OperationResult;
import kd.bos.form.ConfirmCallBackListener;
import kd.bos.form.MessageBoxOptions;
import kd.bos.form.MessageBoxResult;
import kd.bos.form.OpenStyle;
import kd.bos.form.ShowType;
import kd.bos.form.control.Control;
import kd.bos.form.control.Label;
import kd.bos.form.control.events.ClickListener;
import kd.bos.form.events.CustomEventArgs;
import kd.bos.form.events.MessageBoxClosedEvent;
import kd.bos.form.field.BasedataEdit;
import kd.bos.list.ListShowParameter;
import kd.bos.logging.Log;
import kd.bos.logging.LogFactory;
import kd.bos.orm.query.QFilter;
import kd.bos.report.ReportShowParameter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.MetadataServiceHelper;
import kd.bos.servicehelper.QueryServiceHelper;
import kd.bos.servicehelper.inte.InteServiceHelper;
import kd.bos.servicehelper.operation.DeleteServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.bos.url.UrlService;
import kd.tmc.fbp.common.util.EmptyUtil;
import kd.tmc.fs.common.helper.AcctBalanceHelper;
import kd.tmc.fs.common.helper.LspWapper;
import kd.tmc.fs.common.helper.PeriodHelper;
import kd.tmc.fs.common.helper.PermissionHelper;
import kd.tmc.fs.common.helper.SettingMapperHelper;
import kd.tmc.fs.formplugin.AttentionCardPlugin;
import kd.tmc.fs.formplugin.common.DynamicFormPlugin;
import org.apache.commons.lang.StringUtils;

public class AttentionCardPlugin1 extends DynamicFormPlugin implements ClickListener {
    private static Log logger = LogFactory.getLog(AttentionCardPlugin.class);
    private String labCurrencySign;
    private int currencyAmtprecision;
    private Format currencyFormat;

    public AttentionCardPlugin1() {
    }

    private void initFormat() {
        FormatObject fobj = InteServiceHelper.getUserFormat(Long.valueOf(RequestContext.get().getUserId()));
        fobj.getCurrencyFormat().setCurrencySymbols(this.labCurrencySign);
        fobj.getCurrencyFormat().setMinimumFractionDigits(this.currencyAmtprecision);
        this.currencyFormat = FormatFactory.get(FormatTypes.Currency).getFormat(fobj);
    }

    public void registerListener(EventObject e) {
        super.registerListener(e);
        this.addClickListeners(new String[]{"tpv_refresh_btn", "tpv_lookupjournal_btn", "tpv_lookuptransaction_btn", "tpv_addaccountbank_btn", "closeup_btn", "tpv_refresh_btn", "open_btn"});
        this.addClickListeners(new String[]{"tpv_add_pic", "tpv_add_label", "tpv_delete"});
        this.initF7();
        this.getView().getFormShowParameter().setListentimerElapsed(true);
    }

    //银行账户
    private void initF7() {
        BasedataEdit acctBankF7 = (BasedataEdit) this.getControl("accountbankf7");
        if (acctBankF7 != null) {
            acctBankF7.addBeforeF7SelectListener((beforeF7SelectEvent) -> {
            });
        }

    }

    public void afterCreateNewData(EventObject e) {
        super.afterCreateNewData(e);
        List<DynamicObject> userSettings = this.getUserSetting();

        for (int i = 0; i < userSettings.size(); ++i) {
            Long acctBankID = ((DynamicObject) userSettings.get(i)).getLong("accountbank.id");
            this.addAttentionCard(acctBankID);
        }

        this.getView().setVisible(Boolean.FALSE, new String[]{"tpv_lastupdatetime", "open_btn"});
    }

    public void customEvent(CustomEventArgs e) {
        if ("refreshFromGridContainer".equals(e.getKey())) {
        }

    }

    private List<DynamicObject> getUserSetting() {
        String userIdStr = RequestContext.get().getUserId();
        Long userId = Long.valueOf(userIdStr);
        String selectors = "user,accountbank,currency,order";
        DynamicObject[] userSettings = BusinessDataServiceHelper.load("cas_attentionsetting", selectors, new QFilter[]{new QFilter("user", "=", userId)});
        List<DynamicObject> attentionSettings = new ArrayList(userSettings.length);

        for (int i = 0; i < userSettings.length; ++i) {
            attentionSettings.add(userSettings[i]);
        }

        return attentionSettings;
    }

    public void click(EventObject evt) {
        super.click(evt);
        Control control = (Control) evt.getSource();
        switch (control.getKey()) {
            //刷新
            case "tpv_refresh_btn":
                this.refreshSingle();
                break;
            //查账
            case "tpv_lookupjournal_btn":
                this.lookUpJournal();
                break;
            //查交易
            case "tpv_lookuptransaction_btn":
                this.lookUpTransDetail();
                break;
            //图片
            case "tpv_add_pic":
                this.showAddAcctBankView();
                break;
            //新增账户
            case "tpv_add_label":
                this.showAddAcctBankView();
                break;
            //新增账户按钮
            case "tpv_addaccountbank_btn":
                this.showAddAcctBankView();
                break;
            //全部刷新按钮
            case "tpv_refreshall_btn":
                this.refreshAll();
                break;
            //删除
            case "tpv_delete":
                this.confirmBeforeDelete();
        }

    }

    private void confirmBeforeDelete() {
        int rowIndex = this.getModel().getEntryCurrentRowIndex("tpv_acctbankcardentry");
        String bankNumber = (String) this.getModel().getValue("tpv_banknumber", rowIndex);
        String confirmMessage = String.format(ResManager.loadKDString("确认不再关注尾号为%s的银行账户？", "AttentionCardPlugin_0", "fi-cas-formplugin", new Object[0]), bankNumber);
        ConfirmCallBackListener confirmCallBacks = new ConfirmCallBackListener("delete", this);
        this.getView().showConfirm(confirmMessage, MessageBoxOptions.OKCancel, confirmCallBacks);
    }

    public void confirmCallBack(MessageBoxClosedEvent messageBoxClosedEvent) {
        super.confirmCallBack(messageBoxClosedEvent);
        String callBackId = messageBoxClosedEvent.getCallBackId();
        if (MessageBoxResult.Yes.equals(messageBoxClosedEvent.getResult()) && "delete".equals(callBackId)) {
            this.deleteAttentionItem();
        }

    }

    private void deleteAttentionItem() {
        int rowIndex = this.getModel().getEntryCurrentRowIndex("tpv_acctbankcardentry");
        DynamicObject acctBank = this.getDynamicObject("tpv_accountbank", rowIndex);
        DynamicObject currency = this.getDynamicObject("tpv_currency", rowIndex);
        String userIdStr = RequestContext.get().getUserId();
        Long userId = Long.valueOf(userIdStr);
        QFilter acctBankFilter = new QFilter("tpv_accountbank", "=", acctBank.getPkValue());
        QFilter currencyFilter = new QFilter("tpv_currency", "=", currency.getPkValue());
        QFilter userFilter = new QFilter("user", "=", userId);
        DeleteServiceHelper.delete("cas_attentionsetting", new QFilter[]{userFilter, acctBankFilter, currencyFilter});
        this.getModel().deleteEntryRow("tpv_acctbankcardentry", rowIndex);
        int rowCount = this.getModel().getEntryRowCount("tpv_acctbankcardentry");
        if (rowCount == 0) {
            this.getView().setVisible(Boolean.TRUE, new String[]{"add_card"});
        }

    }

    private void refreshAll() {
        int rowCount = this.getModel().getEntryRowCount("tpv_acctbankcardentry");
        if (rowCount == 0) {
            this.getView().showErrorNotification(ResManager.loadKDString("没有重点关注的账户，请添加重点关注账户", "AttentionCardPlugin_6", "fi-cas-formplugin", new Object[0]));
        } else {
            int[] indexArr = new int[rowCount];

            for (int i = 0; i < rowCount; indexArr[i] = i++) {
            }

            this.refreshBalance(indexArr);
            this.getView().setVisible(Boolean.TRUE, new String[]{"tpv_lastupdatetime"});
            this.getModel().setValue("tpv_lastupdatetime", new Date());
            String msg = ResManager.loadKDString("更新于", "AttentionCardPlugin_7", "fi-cas-formplugin", new Object[0]);
            ((Label) this.getView().getControl("premsg")).setText(msg);
        }
    }

    private void refreshSingle() {
        int index = this.getModel().getEntryCurrentRowIndex("tpv_acctbankcardentry");
        this.refreshBalance(new int[]{index});
    }

    private void refreshBalance(int[] rowIndexs) {
        List<OperateErrorInfo> allErrorInfo = new ArrayList();
        List<Object> successPkIds = new ArrayList();

        for (int i = 0; i < rowIndexs.length; ++i) {
            int rowIndex = rowIndexs[i];
            DynamicObject acctBank = this.getDynamicObject("tpv_accountbank", rowIndex);
            Long orgId = acctBank.getLong("company.id");
            boolean hasViewRight = PermissionHelper.hasViewRight(orgId, "bei_bankbalance");
            if (!hasViewRight) {
                String message = String.format(ResManager.loadKDString("没有组织%s银行账户余额的更新权限，请分配后再进行操作！", "AttentionCardPlugin_2", "fi-cas-formplugin", new Object[0]), acctBank.getString("company.name"));
                OperateErrorInfo errorInfo = new OperateErrorInfo();
                errorInfo.setMessage(message);
                allErrorInfo.add(errorInfo);
            } else {
                Long currencyId = this.getPk("tpv_currency", rowIndex);
                OperationResult operationResult = AcctBalanceHelper.onlineUpdate(acctBank.getLong("id"), currencyId);
                if (operationResult.isSuccess()) {
                    successPkIds.add(acctBank.getLong("id"));
                    logger.info(String.format("==银行信息{%s}==", acctBank.toString()));
                    logger.info(String.format("==余额查询{%s}==", AcctBalanceHelper.getLastestBeBalance(acctBank.getLong("id"), currencyId)));
                    this.showBalance(rowIndex, AcctBalanceHelper.getLastestBeBalance(acctBank.getLong("id"), currencyId));
                } else {
                    allErrorInfo.addAll(operationResult.getAllErrorInfo());
                }

                logger.info(String.format("==更新次数{%s},更新结果{%s}==", rowIndex, operationResult.toString()));
            }
        }

        if (allErrorInfo.size() > 0) {
            OperationResult operationResult = new OperationResult();
            operationResult.setSuccess(false);
            operationResult.setBillCount(rowIndexs.length);
            operationResult.setSuccessPkIds(successPkIds);
            operationResult.setShowMessage(true);
            operationResult.setAllErrorInfo(allErrorInfo);
            this.getView().showOperationResult(operationResult);
        } else {
            this.getView().showSuccessNotification(ResManager.loadKDString("联机查询成功", "AttentionCardPlugin_8", "fi-cas-formplugin", new Object[0]));
        }

    }

    private void lookUpJournal() {
        int index = this.getModel().getEntryCurrentRowIndex("tpv_acctbankcardentry");
        DynamicObject acctBank = this.getDynamicObject("tpv_accountbank", index);
        Long orgId = acctBank.getLong("company.id");
        boolean hasViewRight = PermissionHelper.hasViewRight(orgId, "cas_bankjournal");
        if (!hasViewRight) {
            String message = String.format(ResManager.loadKDString("没有组织%s银行日记账的查看权限，请分配后再进行操作！", "AttentionCardPlugin_3", "fi-cas-formplugin", new Object[0]), acctBank.getString("org.name"));
            this.getView().showErrorNotification(message);
        } else {
            DynamicObject period = PeriodHelper.getPeriodByDate(orgId, new Date());
            ReportShowParameter fsp = new ReportShowParameter();
            fsp.setFormId("cas_bankjournalformrpt");
            fsp.setCustomParam("setDefaultFilters", Boolean.TRUE);
            fsp.setCustomParam("org", orgId);
            fsp.setCustomParam("tpv_accountBank", acctBank.getPkValue());
            fsp.setCustomParam("tpv_currency", acctBank.getLong("defaultcurrency.id"));
            fsp.setCustomParam("datatype", 1);
            fsp.setCustomParam("beginperiod", period.getPkValue());
            fsp.setCustomParam("endperiod", period.getPkValue());
            OpenStyle openStyle = new OpenStyle();
            openStyle.setShowType(ShowType.MainNewTabPage);
            fsp.setOpenStyle(openStyle);
            this.getView().showForm(fsp);
        }
    }

    private void lookUpTransDetail() {
        int index = this.getModel().getEntryCurrentRowIndex("tpv_acctbankcardentry");
        DynamicObject acctBank = this.getDynamicObject("tpv_accountbank", index);
        Long orgId = acctBank.getLong("company.id");
        boolean hasViewRight = PermissionHelper.hasViewRight(orgId, "bei_transdetail_cas");
        if (!hasViewRight) {
            String message = String.format(ResManager.loadKDString("没有组织%s交易明细的查看权限，请分配后再进行操作！", "AttentionCardPlugin_4", "fi-cas-formplugin", new Object[0]), acctBank.getString("org.name"));
            this.getView().showErrorNotification(message);
        } else {
            ListShowParameter fsp = new ListShowParameter();
            fsp.setBillFormId("bei_transdetail_cas");
            fsp.setCustomParam("setDefaultFilters", Boolean.TRUE);
            fsp.setCustomParam("company.id", Collections.singletonList(orgId));
            fsp.setCustomParam("accountbank.id", Collections.singletonList(acctBank.getPkValue()));
            fsp.setCustomParam("currency.id", acctBank.getLong("defaultcurrency.id"));
            fsp.setCustomParam("bizdate", 63);
            LspWapper lspWapper = new LspWapper(fsp);
            lspWapper.clearPlugins();
            lspWapper.registerPlugin("kd.tmc.bei.formplugin.detail.TransDetailHyperlinkList");
            OpenStyle openStyle = new OpenStyle();
            openStyle.setShowType(ShowType.MainNewTabPage);
            fsp.setOpenStyle(openStyle);
            fsp.setCaption(ResManager.loadKDString("交易明细列表", "AttentionCardPlugin_5", "fi-cas-formplugin", new Object[0]));
            this.getView().showForm(fsp);
        }
    }

    private void showAddAcctBankView() {
        BasedataEdit acctBankF7 = (BasedataEdit) this.getControl("accountbankf7");
        if (acctBankF7 != null) {
            acctBankF7.click();
        }

    }

    public void propertyChanged(PropertyChangedArgs e) {
        super.propertyChanged(e);
        String key = e.getProperty().getName();
        ChangeData[] changeData = e.getChangeSet();
        Object newValue = changeData[0].getNewValue();
        switch (key) {
            case "accountbankf7":
                this.acctBankChanged(newValue);
            default:
        }
    }

    private void acctBankChanged(Object newValue) {
        if (newValue != null) {
            DynamicObjectCollection acctBanks = (DynamicObjectCollection) newValue;
            Iterator var3 = acctBanks.iterator();

            while (var3.hasNext()) {
                DynamicObject acctBank = (DynamicObject) var3.next();
                acctBank = acctBank.getDynamicObject("fbasedataid");
                this.addAttentionCard(acctBank);
                this.saveAttentionSetting(acctBank);
            }

            this.getModel().setValue("accountbankf7", new Object[0]);
        }
    }

    private void addAttentionCard(Object accountBank) {
        if (accountBank != null) {
            if (!this.existCard(accountBank)) {
                int newIndex = this.getModel().createNewEntryRow("tpv_acctbankcardentry");
                this.setValue("tpv_accountbank", accountBank, newIndex);
                DynamicObject acctBank = null;
                if (accountBank instanceof DynamicObject) {
                    acctBank = (DynamicObject) accountBank;
                } else if (accountBank instanceof Long) {
                    acctBank = this.getDynamicObject("tpv_accountbank", newIndex);
                }

                if (acctBank == null) {
                    this.getModel().deleteEntryRow("tpv_acctbankcardentry", newIndex);
                } else {
                    this.setValue("tpv_currency", acctBank.getLong("defaultcurrency.id"), newIndex);
                    String bankName = acctBank.getString("bank.name");
                    this.setValue("tpv_bankname", bankName, newIndex);
                    DynamicObject bank = QueryServiceHelper.queryOne("bd_finorginfo", "bank_cate", new QFilter[]{new QFilter("id", "=", acctBank.getLong("bank.id"))});
                    String acctBankNumber;
                    String bankOrgName;
                    if (bank != null) {
                        acctBankNumber = SettingMapperHelper.getValue("finorgimg", bank.getString("bank_cate"));
                        String bkImg = acctBankNumber != null ? acctBankNumber : "/images/pc/cardbackground/card_otherbank_280_150.png";
                        bankOrgName = UrlService.getDomainContextUrl();
                        if (bankOrgName.endsWith("/")) {
                            bankOrgName = bankOrgName.substring(0, bankOrgName.length() - 1);
                        }

                        this.setValue("tpv_backgroundimg", bankOrgName + bkImg, newIndex);
                    }

                    acctBankNumber = acctBank.getString("bankaccountnumber");
                    if (acctBankNumber.length() > 4) {
                        acctBankNumber = acctBankNumber.substring(acctBankNumber.length() - 4);
                    }

                    acctBankNumber = String.format("(%s)", acctBankNumber);
                    this.setValue("tpv_banknumber", acctBankNumber, newIndex);
                    DynamicObject bankOrg = acctBank.getDynamicObject("company");
                    bankOrgName = null;
                    if (EmptyUtil.isNoEmpty(bankOrg)) {
                        bankOrgName = bankOrg.getLocaleString("name").toString();
                    }

                    this.setValue("tpv_bankorg", bankOrgName, newIndex);
                    Long currencyId = this.getPk("tpv_currency", newIndex);
                    DynamicObject lastestBalanceInquire = AcctBalanceHelper.getLastestBeBalance(acctBank.getLong("id"), currencyId);
                    this.showBalance(newIndex, lastestBalanceInquire);
                    this.getView().setVisible(false, new String[]{"add_card"});
                }
            }
        }
    }

    private void showBalance(int newIndex, DynamicObject lastestBalanceInquire) {
        if (lastestBalanceInquire != null) {
            BigDecimal balance = lastestBalanceInquire.getBigDecimal("amount");
            DynamicObject currency = this.getDynamicObject("tpv_currency", newIndex);
            String balanceStr = this.formatAmount(balance, currency);
            this.setValue("balance", balanceStr, newIndex);
            this.initFormat();
            Date lastUpdateTime = lastestBalanceInquire.getDate("modifytime");
            this.setValue("updatetime", lastUpdateTime, newIndex);
        }

    }

    private void saveAttentionSetting(DynamicObject acctBank) {
        String userIdStr = RequestContext.get().getUserId();
        Long userId = Long.valueOf(userIdStr);
        DynamicObject[] userSettings = BusinessDataServiceHelper.load("cas_attentionsetting", "order", new QFilter[]{new QFilter("user", "=", userId)}, "order");
        int order = 0;
        if (userSettings.length > 0) {
            order = userSettings[userSettings.length - 1].getInt("order") + 1;
        }

        MainEntityType dt = MetadataServiceHelper.getDataEntityType("cas_attentionsetting");
        DynamicObject attentionItem = new DynamicObject(dt);
        attentionItem.set("user", userId);
        attentionItem.set("accountbank", acctBank.getPkValue());
        attentionItem.set("currency", acctBank.getLong("defaultcurrency.id"));
        attentionItem.set("order", order);
        SaveServiceHelper.save(new DynamicObject[]{attentionItem});
    }

    private boolean existCard(Object acctBank) {
        int rowCount = this.getModel().getEntryRowCount("tpv_acctbankcardentry");
        Long acctBankId = null;
        if (acctBank instanceof DynamicObject) {
            acctBankId = Long.valueOf(((DynamicObject) acctBank).getPkValue().toString());
        } else if (acctBank instanceof Long) {
            acctBankId = (Long) acctBank;
        }

        for (int i = 0; i < rowCount; ++i) {
            DynamicObject entryAccountBank = this.getDynamicObject("tpv_accountbank", i);
            if (entryAccountBank != null && acctBankId != null && acctBankId.equals(entryAccountBank.getLong("id"))) {
                return true;
            }
        }

        return false;
    }

    private String formatAmount(BigDecimal amount, DynamicObject currency) {
        if (amount == null) {
            amount = BigDecimal.ZERO;
        }

        String currencySign = "";
        int amountPrecition = 4;
        if (currency != null) {
            currencySign = currency.getString("sign");
            amountPrecition = currency.getInt("amtprecision");
            amount = amount.setScale(amountPrecition);
            this.labCurrencySign = currency.getString("sign");
            this.currencyAmtprecision = amountPrecition;
        }

        String formatStr = "###,##0." + StringUtils.repeat("0", amountPrecition);
        DecimalFormat decimalFormat = new DecimalFormat(formatStr);
        String amountStr = decimalFormat.format(amount);
        return currencySign + amountStr;
    }
}
