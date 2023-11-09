package com.pur.formplugin;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.entity.plugin.AbstractOperationServicePlugIn;
import kd.bos.entity.plugin.args.BeforeOperationArgs;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VerifySave extends AbstractOperationServicePlugIn {


    @Override
    public void beforeExecuteOperationTransaction(BeforeOperationArgs e) {
        super.beforeExecuteOperationTransaction(e);
        String operationKey = e.getOperationKey();
        switch (operationKey) {
            //监听点击保存按钮
            case "save":
                verifySaveDate(e);
                break;
            default:
                break;

        }
    }

    public void verifySaveDate(BeforeOperationArgs e) {
        DynamicObject[] entities = e.getDataEntities();
        for (DynamicObject bill: entities) {
            //获取单据头集合


            DynamicObjectCollection entry = bill.getDynamicObjectCollection("tpv_gjtt_purchase_cg");
            //校验分录不为空
            if (entry.isEmpty()|| entry.get(0).get("tpv_materia")==null){
                e.setCancel(true);
                e.setCancelMessage("分录不能为空");
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            //查询当前日期
            Date currentDate = new Date();
            //将当前日期转换为年月日字符串类型
            String currentDateString = sdf.format(currentDate);

            for (DynamicObject entity: entry)
            {
                //拿到物料信息
                DynamicObject Materia = (DynamicObject) entity.get("tpv_materia");
                //拿到采购信息金额
                BigDecimal amount = entity.getBigDecimal("tpv_amount");
                //拿到需求日期
                Date demandDate = entity.getDate("tpv_requiredate");
                //判断需求日期是否为空
                if (demandDate==null) {
                    e.setCancel(true);
                    e.setCancelMessage("采购信息需求日期不能不能为空");
                }else {
                    //将需求日期转换为年月日字符串类型
                    String demandDateString = sdf.format(demandDate);
                    Boolean equivalence = demandDateString.equals(currentDateString);
                    if (equivalence.equals(false)) {
                        //判断需求日期是都小于当前日期
                        if (demandDate.before(currentDate) && amount.compareTo(BigDecimal.ZERO) <= 0 && Materia == null){
                            e.setCancel(true);
                            e.setCancelMessage("采购信息需求日期不能小于当前日期,采购金额需大于0，物料不为空");
                        }else if (demandDate.before(currentDate)) {
                            e.setCancel(true);
                            e.setCancelMessage("采购信息需求日期不能小于当前日期");
                        }else if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                            e.setCancel(true);
                            e.setCancelMessage("采购金额需大于0");
                        }
                    }else {
                        if (amount.compareTo(BigDecimal.ZERO) <= 0 && Materia == null) {
                            e.setCancel(true);
                            e.setCancelMessage("采购金额需大于0，物料不为空");
                        }else {
                            return;
                        }
                    }
                }
            }
        }
    }
}
