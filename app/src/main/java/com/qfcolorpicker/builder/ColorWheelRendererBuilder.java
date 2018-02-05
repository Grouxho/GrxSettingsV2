package com.qfcolorpicker.builder;

import com.qfcolorpicker.ColorPickerView;
import com.qfcolorpicker.renderer.ColorWheelRenderer;
import com.qfcolorpicker.renderer.FlowerColorWheelRenderer;
import com.qfcolorpicker.renderer.SimpleColorWheelRenderer;

public class ColorWheelRendererBuilder {
	public static ColorWheelRenderer getRenderer(ColorPickerView.WHEEL_TYPE wheelType) {
		switch (wheelType) {
			case CIRCLE:
				return new SimpleColorWheelRenderer();
			case FLOWER:
				return new FlowerColorWheelRenderer();
		}
		throw new IllegalArgumentException("wrong WHEEL_TYPE");
	}
}