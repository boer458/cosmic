package com.pur.formplugin;

import com.alibaba.druid.util.StringUtils;
import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.control.events.BeforeItemClickEvent;
import kd.bos.form.control.events.ItemClickEvent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ItemClick extends AbstractBillPlugIn {
    @Override
    public void itemClick(ItemClickEvent evt){
        super.itemClick(evt);
        String itemKey = evt.getItemKey();
        //判断是否为检查必填按钮
        if (StringUtils.equals("tpv_baritemap2", itemKey)){
            // 获取到采购组织
            DynamicObject billno = (DynamicObject) this.getView().getModel().getValue("tpv_org");
            // 获取申请部门
            DynamicObject applyorg = (DynamicObject) this.getView().getModel().getValue("tpv_applyorg");
            // 获取申请人
            DynamicObject applier = (DynamicObject) this.getView().getModel().getValue("tpv_applier");
            // 获取汇率表
            DynamicObject exrateTable = (DynamicObject) this.getView().getModel().getValue("tpv_exratetable");
            // 获取结算币
            DynamicObject tocurr = (DynamicObject) this.getView().getModel().getValue("tpv_tocurr");
            // 获取汇率日期
            Date exratedate = (Date) this.getView().getModel().getValue("tpv_exratedate");
            List list = new ArrayList<>();
            //判断是否为空
            if (billno==null){
                String string = new String("采购组织不能为空");
                list.add(billno);
            }
            // 判断申请部门是否为空
            if (applyorg == null) {
                String applyorgString = new String("必填项申请部门为空");
                list.add(applyorgString);
            }
            // 判断申请人是否为空
            if (applier == null) {
                String applierString = new String("必填项申请人为空");
                list.add(applierString);
            }
            // 判断汇率表是否为空
            if (exrateTable == null) {
                String exrateTableString = new String("必填项汇率表为空");
                list.add(exrateTableString);
            }
            // 判断结算币是否为空
            if (tocurr == null) {
                String tocurrString = new String("必填项结算币为空");
                list.add(tocurrString);
            }
            // 判断汇率日期是否为空
            if (exratedate == null) {
                String exratedateString = new String("必填项汇率日期为空");
                list.add(exratedateString);
            }
            String result = String.join(",", list);
            if (list.size()==0){
                this.getView().showMessage("基本信息已全部填写完毕");
            }else {

                this.getView().showMessage(result);
            }


        }

    }
    @Override
    public void beforeItemClick(BeforeItemClickEvent evt) {
        // TODO Auto-generated method stub
        super.beforeItemClick(evt);
        String itemKey = evt.getItemKey();
        // 判断是否为提交按钮
        if (StringUtils.equals("bar_submit", itemKey)) {
            this.submitIncident(evt);


        }

    }

    public void submitIncident(BeforeItemClickEvent evt) {
        // 拿到单据用途字段
        String usage = (String) this.getView().getModel().getValue("tpv_usage");
        // 判断用途是否为空
        if (usage.isEmpty()) {
            evt.setCancel(true);
            this.getView().showErrorNotification("提交时用途不可为空");
        }
    }


}
