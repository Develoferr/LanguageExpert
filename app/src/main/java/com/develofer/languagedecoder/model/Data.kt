package com.develofer.languagedecoder.model

data class Data (val detections: List<Detection>) {
    init {
        for (Detection in detections) {
            //assignmentCompleteName = allLanguages.find {it.code == Detection.Language.nameCode}
            //Detection.Language.completeName = assignmentCompleteName
        }
    }
}