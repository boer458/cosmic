package com.pur.formplugin;

import kd.bos.form.events.SetFilterEvent;
import kd.bos.list.plugin.AbstractListPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.user.UserServiceHelper;

import java.util.List;

public class SetListFilter extends AbstractListPlugin {
    @Override
    public void setFilter(SetFilterEvent e) {
        long userId = UserServiceHelper.getCurrentUserId();// 获取用户id
        List<QFilter> qFilterList = e.getQFilters();// 获取过滤规则列表
        qFilterList.clear();// 清空过滤规则
        qFilterList.add(new QFilter("tpv_supplier.name", "!=", null));// 只查看供应商名称不为空的单据
        qFilterList.add(new QFilter("tpv_applier", "=", userId));// 只能查看当前用户自己创建的
        String[] status = new String[] { "A","B","C" };
        qFilterList.add(new QFilter("billstatus", QCP.in, status));// 只查看单据状态为已审核或已关闭的单据

    }
}
