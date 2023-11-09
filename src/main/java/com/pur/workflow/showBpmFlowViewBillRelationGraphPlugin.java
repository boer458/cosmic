package com.pur.workflow;

import kd.bos.entity.datamodel.IDataModel;
import kd.bos.form.FormShowParameter;
import kd.bos.form.IFormView;
import kd.bos.form.IPageCache;
import kd.bos.form.control.Control;
import kd.bos.form.control.events.BeforeClickEvent;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.form.events.*;
import kd.bos.workflow.bpm.monitor.plugin.BpmFlowViewBillRelationGraphPlugin;

import kd.bos.workflow.design.plugin.IWorkflowDesigner;
import kd.bos.workflow.engine.*;

import java.util.EventObject;
import java.util.Map;

public class showBpmFlowViewBillRelationGraphPlugin extends BpmFlowViewBillRelationGraphPlugin implements IWorkflowDesigner {


    public showBpmFlowViewBillRelationGraphPlugin() {
        super();
    }

    @Override
    public IFormView getView() {
        return super.getView();
    }

    @Override
    public String getPluginName() {
        return super.getPluginName();
    }

    @Override
    public void setPluginName(String name) {
        super.setPluginName(name);
    }

    @Override
    public void setView(IFormView formView) {
        super.setView(formView);
    }

    @Override
    protected RepositoryService getRepositoryService() {
        return super.getRepositoryService();
    }

    @Override
    protected TaskService getTaskService() {
        return super.getTaskService();
    }

    @Override
    protected RuntimeService getRuntimeService() {
        return super.getRuntimeService();
    }

    @Override
    protected ManagementService getManagementService() {
        return super.getManagementService();
    }

    @Override
    protected HistoryService getHistoryService() {
        return super.getHistoryService();
    }

    @Override
    public void showForm(String key, String formId) {
        super.showForm(key, formId);
    }

    @Override
    public void showForm(String key, String formId, String caption) {
        super.showForm(key, formId, caption);
    }

    @Override
    public void showForm(String key, String formId, Map<String, Object> params) {
        super.showForm(key, formId, params);
    }

    @Override
    public void showForm(String key, String formId, String caption, Map<String, Object> params) {
        super.showForm(key, formId, caption, params);
    }

    @Override
    public void showForm(FormShowParameter parameter) {
        super.showForm(parameter);
    }

    @Override
    protected void showComingMessage() {
        super.showComingMessage();
    }

    @Override
    protected FormShowParameter getShowParameter(String key, String formId) {
        return super.getShowParameter(key, formId);
    }

    @Override
    protected FormShowParameter getShowParameter(String key, String formId, String caption) {
        return super.getShowParameter(key, formId, caption);
    }

    @Override
    protected FormShowParameter getShowParameter(String key, String formId, Map<String, Object> params) {
        return super.getShowParameter(key, formId, params);
    }

    @Override
    protected FormShowParameter getShowParameter(String key, String formId, String caption, Map<String, Object> params) {
        return super.getShowParameter(key, formId, caption, params);
    }

    @Override
    public String showFormInContainer(String formId, String targetKey, Map<String, Object> params) {
        return super.showFormInContainer(formId, targetKey, params);
    }

    @Override
    protected FormShowParameter getShowFormInContainerParameter(String formId, String targetKey, Map<String, Object> params) {
        return super.getShowFormInContainerParameter(formId, targetKey, params);
    }

    @Override
    public void initialize() {
        super.initialize();
    }

    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
    }

    @Override
    protected IDataModel getModel() {
        return super.getModel();
    }

    @Override
    public IPageCache getPageCache() {
        return super.getPageCache();
    }

    @Override
    public void preOpenForm(PreOpenFormEventArgs e) {
        super.preOpenForm(e);
    }

    @Override
    public void onCreateDynamicUIMetas(OnCreateDynamicUIMetasArgs e) {
        super.onCreateDynamicUIMetas(e);
    }

    @Override
    public void loadCustomControlMetas(LoadCustomControlMetasArgs e) {
        super.loadCustomControlMetas(e);
    }

    @Override
    public void onGetControl(OnGetControlArgs e) {
        super.onGetControl(e);
    }

    @Override
    protected boolean canDoOperation(Long orgId) {
        return super.canDoOperation(orgId);
    }

    @Override
    public void afterCreateNewData(EventObject e) {
        super.afterCreateNewData(e);
    }

    @Override
    public void itemClick(ItemClickEvent evt) {
        super.itemClick(evt);
    }

    @Override
    public void messageBoxClosed(MessageBoxClosedEvent e) {
        super.messageBoxClosed(e);
    }

    @Override
    public void setCollapsible(String key, boolean isCoollapsible) {
        super.setCollapsible(key, isCoollapsible);
    }

    @Override
    public void flexBeforeClosed(FlexBeforeClosedEvent e) {
        super.flexBeforeClosed(e);
    }

    @Override
    public void trace(Object object) {
        super.trace(object);
    }

    @Override
    public Map<String, Object> getDesignerInitData(Map<String, Object> params) {
        return super.getDesignerInitData(params);
    }

    @Override
    public void refreshPage() {
        super.refreshPage();
    }

    @Override
    public void closedCallBack(ClosedCallBackEvent e) {
        super.closedCallBack(e);
    }

    @Override
    public void confirmCallBack(MessageBoxClosedEvent messageBoxClosedEvent) {
        super.confirmCallBack(messageBoxClosedEvent);
    }

    @Override
    public void afterBindData(EventObject e) {
        super.afterBindData(e);
    }

    @Override
    public void beforeBindData(EventObject e) {
        super.beforeBindData(e);
    }

    @Override
    public void addClickListeners(String... keys) {
        super.addClickListeners(keys);
    }

    @Override
    public void addItemClickListeners(String... keys) {
        super.addItemClickListeners(keys);
    }

    @Override
    public <T extends Control> T getControl(String key) {
        return super.getControl(key);
    }

    @Override
    public void beforeClick(BeforeClickEvent evt) {
        super.beforeClick(evt);
    }

    @Override
    public void click(EventObject evt) {
        super.click(evt);
    }

    @Override
    public void beforeDoOperation(BeforeDoOperationEventArgs args) {
        super.beforeDoOperation(args);
    }

    @Override
    public void afterDoOperation(AfterDoOperationEventArgs afterDoOperationEventArgs) {
        super.afterDoOperation(afterDoOperationEventArgs);
    }

    @Override
    public void contextMenuClick(ContextMenuClickEvent e) {
        super.contextMenuClick(e);
    }

    @Override
    public void customPrintDataObject(CustomPrintDataObjectArgs e) {
        super.customPrintDataObject(e);
    }

    @Override
    public void destory() {
        super.destory();
    }

    @Override
    public void updateBillSummaryAndApprovalRecored(String historicActivityInstanceId) {
        super.updateBillSummaryAndApprovalRecored(historicActivityInstanceId);
    }

    @Override
    protected void updateBillSummaryAndApprovalRecored(String historicActivityInstanceId, String entityNumber, String businessKey, boolean openByBillCard) {
        super.updateBillSummaryAndApprovalRecored(historicActivityInstanceId, entityNumber, businessKey, openByBillCard);
    }

    @Override
    public void updateProcInstId(String procInstId, Map<String, Object> data) {
        super.updateProcInstId(procInstId, data);
    }

    @Override
    public void handleCustomEvent(String type, Map<String, Object> param) {
        super.handleCustomEvent(type, param);
    }

    @Override
    protected void switchButtonVisibility(Long procInstId) {
        super.switchButtonVisibility(procInstId);
    }

    @Override
    public void beforeClosed(BeforeClosedEvent e) {
        super.beforeClosed(e);
    }
}
