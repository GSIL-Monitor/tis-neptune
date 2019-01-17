/* 
 * The MIT License
 *
 * Copyright (c) 2018-2022, qinglangtech Ltd
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.qlangtech.tis.solrextend.queryparse.s4supplyStatementAudit;

/*
 * 供应商版供应链2.5期-应收/应付账款，相关定义的常量 <br>
 * http://k.2dfire.net/pages/viewpage.action?pageId=473006169
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public interface ISupplyStatementAuditConstant {

    public static final String KEY_AMOUNT_ACCUMULATOR = "amount_accumulator";

    public static final String KEY_ALL_SUMMARY_TYPE_3_AND_4_SUM = "allsummaryType3And4Sum";

    public static final String KEY_ALL_SUMMARY_Status3_AND_Not_COUNT = "allStatusType3AndNot3count";

    // 单据已结单的状态
    public static final int STATUS_IN_STORAGE_3 = 3;

    // 入库单
    public static final int TYPE_IN_STORAGE_3 = 3;

    // 配送金额
    public static final int TYPE_IN_STORAGE_2 = 2;

    // 退货单
    public static final int TYPE_REFUND_4 = 4;

    public static final String FIELD_KEY_GROUP_COLUMN_TRADE_ID = "trade_id";

    public static final String FIELD_KEY_GROUP_COLUMN_SUPPLY_TYPE_ID = "supplier_type_id";

    public static final String FIELD_KEY_AMOUNT = "amount";

    public static final String FIELD_KEY_TYPE = "type";

    public static final String FIELD_KEY_STATUS = "status";

    public static final String RETURN_FIELD_type3SumAmount = "type3sum";

    public static final String RETURN_FIELD_type2SumAmount = "type2sum";

    public static final String RETURN_FIELD_type4SumAmount = "type4sum";

    public static final String RETURN_FIELD_type3CountAmount = "type3count";

    public static final String RETURN_FIELD_type2CountAmount = "type2count";

    public static final String RETURN_FIELD_type4CountAmount = "type4count";

    public static final String RETURN_FIELD_Status3Count = "status3count";

    public static final String RETURN_FIELD_StatusNot3Count = "statusNot3count";

    public static final String RETURN_FIELD_difference = "type3_4_sum_diff";

    public static final String[] RETURN_FIELDS = new String[] { RETURN_FIELD_type2SumAmount, RETURN_FIELD_type3SumAmount, RETURN_FIELD_type4SumAmount, RETURN_FIELD_difference, RETURN_FIELD_type3CountAmount, RETURN_FIELD_type2CountAmount, RETURN_FIELD_type4CountAmount };
}
