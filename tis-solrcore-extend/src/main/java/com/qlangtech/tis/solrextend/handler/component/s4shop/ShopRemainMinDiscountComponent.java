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
package com.qlangtech.tis.solrextend.handler.component.s4shop;

import org.apache.solr.handler.component.ResponseBuilder;
import java.io.IOException;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class ShopRemainMinDiscountComponent extends ShopMinDiscountComponent {

    public static final String COMPONENT_NAME = "shop_remain_mindiscount";

    @Override
    public void prepare(ResponseBuilder responseBuilder) throws IOException {
        super.prepare(responseBuilder);
    }

    @Override
    public void process(ResponseBuilder responseBuilder) throws IOException {
        super.process(responseBuilder);
    }

    @Override
    public String getDescription() {
        return ShopRemainMinDiscountComponent.COMPONENT_NAME;
    }

    @Override
    protected void updateShopMindiscount(long remainNum, long salesNum, long totalNum, long discountNum, ShopMindiscount shopMindiscount) {
        if (discountNum > shopMindiscount.discount) {
            shopMindiscount.flash(remainNum, salesNum, totalNum, discountNum);
        } else if (discountNum == shopMindiscount.discount) {
            shopMindiscount.updateMindiscountStockNum(remainNum, salesNum, totalNum);
        }
    }

    @Override
    protected boolean isValid(ShopMindiscount shopMindiscount) {
        return !(shopMindiscount.remainNum == 0);
    }
}
