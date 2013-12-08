/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.di2356.glass.btcprice;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

public class PriceView extends FrameLayout {

    private TextView mPriceView;

    public PriceView(Context context) {
        this(context, null, 0);
    }

    public PriceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PriceView(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        LayoutInflater.from(context).inflate(R.layout.btc_price, this);

        mPriceView =  (TextView) findViewById(R.id.price_text);
    }

    public void setPrice(String price) {
    	mPriceView.setText(price);
    }
}
