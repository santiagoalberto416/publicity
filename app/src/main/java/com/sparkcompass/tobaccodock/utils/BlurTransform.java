package com.sparkcompass.tobaccodock.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

//import android.support.v8.renderscript.Allocation;
//import android.support.v8.renderscript.Element;
//import android.support.v8.renderscript.RenderScript;
//import android.support.v8.renderscript.ScriptIntrinsicBlur;

/**
 * Created by Aaron Vega on 6/10/15.
 */
public class BlurTransform implements Transformation {

//    RenderScript rs;
    public BlurTransform(Context context) {
        super();
//        rs = RenderScript.create(context);
    }

    @Override
    public Bitmap transform(Bitmap bitmap) {
        // Create another bitmap that will hold the results of the filter.
        Bitmap blurredBitmap = Bitmap.createBitmap(bitmap);

        // Allocate memory for Renderscript to work with
//        Allocation input = Allocation.createFromBitmap(rs, bitmap, Allocation.MipmapControl.MIPMAP_FULL, Allocation.USAGE_SHARED);
//        Allocation output = Allocation.createTyped(rs, input.getType());
//
//        // Load up an instance of the specific script that we want to use.
//        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
//        script.setInput(input);
//
//        // Set the blur radius
//        script.setRadius(10);
//
//        // Start the ScriptIntrinisicBlur
//        script.forEach(output);
//
//        // Copy the output to the blurred bitmap
//        output.copyTo(blurredBitmap);

        return blurredBitmap;
    }

    @Override
    public String key() {
        return "blur";
    }

}
