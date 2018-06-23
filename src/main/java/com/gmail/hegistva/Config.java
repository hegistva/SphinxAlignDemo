package com.gmail.hegistva;

import java.io.File;

public class Config {

    public static String getModelPath(String lang, ModelType mt) throws IllegalArgumentException {

        File modelRoot = new File("/home/hegistva/miniconda3/envs/tensorflow/lib/python3.5/site-packages/pocketsphinx/model");

        Language l = Language.valueOf(lang);

        if (l == Language.eng) {
            File languageRoot = new File(modelRoot, l.toString());
            if (mt == ModelType.ACOUSTIC) {
                return new File(languageRoot, "cmusphinx-en-us-8khz-5.2").toString();
            } else if (mt == ModelType.DICIONARY) {
                return new File(languageRoot, "cmudict-en-us.dict").toString();
            } else if (mt == ModelType.LANGUAGE) {
                return new File(languageRoot, "en-us.lm.bin").toString();
            }
        } else if (l == Language.fra) {
            File languageRoot = new File(modelRoot, l.toString());
            if (mt == ModelType.ACOUSTIC) {
                return new File(languageRoot, "cmusphinx-fr-ptm-8khz-5.2").toString();
            } else if (mt == ModelType.DICIONARY) {
                return new File(languageRoot, "fr.dict").toString();
            } else if (mt == ModelType.LANGUAGE) {
                return new File(languageRoot, "fr-small.lm.bin").toString();
            }
        }
        throw new IllegalArgumentException("Wrong language");
    }
}
