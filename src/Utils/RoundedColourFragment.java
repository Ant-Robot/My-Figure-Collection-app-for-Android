/*
 * Copyright (C) 2011 The Android Open Source Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package Utils;

import net.myfigurecollection.R;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class RoundedColourFragment extends Fragment
{
	private android.widget.LinearLayout.LayoutParams	lp;
	private int											mColor;
	private int											mMarginBottom;
	private int											mMarginLeft;
	private int											mMarginRight;
	private int											mMarginTop;
	private View										mView;
	private float										mWeight;

	// need a public empty constructor for framework to instantiate
	public RoundedColourFragment()
	{}

	public RoundedColourFragment(int color, float weight, int margin_left, int margin_right, int margin_top, int margin_bottom)
	{
		mColor = color;
		mWeight = weight;
		mMarginLeft = margin_left;
		mMarginRight = margin_right;
		mMarginTop = margin_top;
		mMarginBottom = margin_bottom;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mView = new View(getActivity());

		GradientDrawable background = (GradientDrawable) getResources().getDrawable(R.drawable.rounded_rect);
		background.setColor(mColor);

		mView.setBackgroundDrawable(background);
		lp = new LinearLayout.LayoutParams(0, LayoutParams.FILL_PARENT, mWeight);
		lp.setMargins(mMarginLeft, mMarginTop, mMarginRight, mMarginBottom);
		mView.setLayoutParams(lp);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return mView;
	}

	public void setWeight(float f)
	{
		lp.weight = f;
		mView.setLayoutParams(lp);

	}
}
