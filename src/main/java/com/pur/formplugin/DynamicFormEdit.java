package com.pur.formplugin;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.entity.EntityMetadataCache;
import kd.bos.form.FormShowParameter;
import kd.bos.form.control.Control;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
/**
 * 实现附件回填功能： 附件表单 页面监听插件
 */

public class DynamicFormEdit extends AbstractBillPlugIn {
    // 监听页面按钮点击
    @Override
    public void registerListener(EventObject e) {
        // TODO Auto-generated method stub
        super.registerListener(e);
        this.addClickListeners("btncancel", "btnok"); // 监听确认和取消按钮
    }

    @Override
    public void click(EventObject evt) {
        // TODO Auto-generated method stub
        super.click(evt);
        // 获取被点击的控件对象
        Control source = (Control) evt.getSource();
        // 点击确认按钮
        if (StringUtils.equals(source.getKey(), "btnok")) {
            HashMap<String, Object> hashMap = new HashMap<>();
            // 如果被点击控件为确认，则获取页面相关控件值，组装数据传入returnData返回给父页面，最后关闭页面
             //获取申请人
             DynamicObject applier = (DynamicObject) this.getModel().getValue("tpv_applier");
             //获取申请部门编码
            DynamicObject applyOrg = (DynamicObject) this.getModel().getValue("tpv_applyorg");
            String usage = (String) this.getModel().getValue("tpv_usage");
//            // 获取用途数据
//            String usage = (String) this.getModel().getValue("tpv_textfield");
//            // 获取供应商
//            DynamicObject basedatafield = (DynamicObject) this.getModel().getValue("tpv_basedatafield");
//            // 获取币别
//            DynamicObject currency = (DynamicObject) this.getModel().getValue("tpv_currencyfield");
            // 判断获得的各项数据

              if (applier != null) {
                  hashMap.put("tpv_applier",applier.getLong("id"));
              }

            if (applyOrg != null) {
                hashMap.put("tpv_applyorg", applyOrg.getLong("id"));
            }
            if (usage != null) {
                hashMap.put("tpv_usage", usage);
            }
//            if (basedatafield != null) {
//                hashMap.put("tpv_basedatafield", basedatafield.getLong("id"));
//            }
//            if (currency != null) {
//                hashMap.put("tpv_currencyfield", currency.getLong("id"));
//            }
//            if (usage != null) {
//                hashMap.put("tpv_textfield", usage);
//            }
            // 获取附件信息
            // 获取附件字段的值
            DynamicObject sourceBill2 = this.getModel().getDataEntity(true);
            DynamicObjectCollection sourceAttachcol = (DynamicObjectCollection) sourceBill2.get("tpv_attachmentfield");
            if (sourceAttachcol == null && !sourceAttachcol.isEmpty()) {
                this.getView().returnDataToParent(hashMap);
                this.getView().close();
                return;
            }
            // 获取源附件字段附件对象id集合
            List<Long> attchIdSet = new ArrayList<>();
            sourceAttachcol.forEach(attach -> {
                attchIdSet.add(attach.getDynamicObject("fbasedataId").getLong("id"));
            });
            // 判断是否为空
            if (!attchIdSet.isEmpty()) {
                hashMap.put("tpv_attachmentfield", attchIdSet);
            }
            this.getView().returnDataToParent(hashMap);
            this.getView().close();
        } else if (StringUtils.equals(source.getKey(), "btncancel")) {
            // 被点击控件为取消则设置返回值为空并关闭页面（在页面关闭回调方法中必须验证返回值不为空，否则会报空指针）
            this.getView().returnDataToParent(null);
            this.getView().close();
        }
    }

    /**
     * 打开动态表单之后获取原单数据
     */
    @Override
    public void afterCreateNewData(EventObject e) {
        // TODO Auto-generated method stub
        super.afterCreateNewData(e);
        // 打开动态表单
        FormShowParameter showParameter = this.getView().getFormShowParameter();
        Map<String, Object> maps = new HashMap<>();
        maps = showParameter.getCustomParams();
         Long applier = (Long) maps.get("tpv_applier");//用户
        Integer applyorg = (Integer) maps.get("tpv_applyorg");// 部门
        String usage = (String) maps.get("tpv_usage");
//        Long basedatafield = (Long) maps.get("tpv_supplier");// 供应商
//        Integer currency = (Integer) maps.get("tpv_currency");// 币种
//        String usage = (String) maps.get("tpv_usage");// 用途
         this.getModel().setValue("tpv_applier", applier);
        this.getModel().setValue("tpv_applyorg", applyorg);
        this.getModel().setValue("tpv_usage", usage);
//        this.getModel().setValue("tpv_basedatafield", basedatafield);
//        this.getModel().setValue("tpv_currencyfield", currency);
//        this.getModel().setValue("tpv_textfield", usage);
        List<Long> attchIdSet = (List<Long>) maps.get("tpv_attachmentfield");
        if (attchIdSet != null && !attchIdSet.isEmpty()) {
            this.getModel().setValue("tpv_attachmentfield", attchIdSet.toArray());
        }
    }

}
