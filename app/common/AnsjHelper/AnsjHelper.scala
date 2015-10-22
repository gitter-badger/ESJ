/*
 * Copyright (c) 2015, BoDao, Inc. All Rights Reserved.
 *
 * Author@ dgl
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.
 *
 * you can Segmentation chinase Conveniently by this software.
 *
 * update user dictionary Dynamically
 * set user dictionary path
 *
 */


package common.AnsjHelper


import org.ansj.splitWord.analysis.{NlpAnalysis, ToAnalysis}
import org.ansj.domain.Term
import org.ansj.library.UserDefineLibrary;
import org.ansj.recognition.NatureRecognition
import org.ansj.util.{FilterModifWord, MyStaticValue}
import java.util.List;


object AnsjHelper {
    var dicUpdated = 0;
    MyStaticValue.userLibrary = "./DynConfig/user_dictionary.dic";

    def setUserLibrary(lib: String): Unit = {
        MyStaticValue.userLibrary = lib;
    }

    def updateUserLibrary(dic: String): Unit = {
        UserDefineLibrary.loadLibrary(UserDefineLibrary.FOREST, dic);
    }

    def setUdateLib(): Unit = {
        dicUpdated = 1;
    }

    def strParse(str: String): List[Term] = {
        if (dicUpdated == 1) {
            updateUserLibrary("./DynConfig/user_dictionary.dic");
            dicUpdated = 0;
        }

        return ToAnalysis.parse(str);
    }
}
