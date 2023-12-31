package com.pur.workflow;

import com.kingdee.util.StringUtils;
import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.form.FormShowParameter;
import kd.bos.form.ShowType;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.workflow.form.operate.flowchart.ViewFlowchartConstant;

import java.util.HashMap;

public class ViewItemClick extends AbstractBillPlugIn {
    @Override
    public void itemClick(ItemClickEvent e) {
        super.itemClick(e);
        String key = e.getItemKey();
        if (StringUtils.equals("tpv_view_chart", key)) {
            FormShowParameter parameter = new FormShowParameter();
            parameter.setFormId("tpv_app_viewflow");
            DynamicObjectCollection treeData = this.getModel().getEntryEntity("tpv_treeentryentity");
            if (treeData != null && !treeData.isEmpty()) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("entity", treeData);
                parameter.setCustomParams(map);
                parameter.setClientParam(ViewFlowchartConstant.PROCINSTID, treeData);
                parameter.getOpenStyle().setShowType(ShowType.MainNewTabPage);
                this.getView().showForm(parameter);
            } else {
                this.getView().showTipNotification("树形单据体内容不能为空");
            }
        }
    }
}