package com.example.medicalapp.ui.report

import com.example.medicalapp.data.Status
import com.example.medicalapp.data.Status.SUCCESS
import com.example.medicalapp.data.Status.ERROR
import com.example.medicalapp.data.Status.LOADING
import com.example.medicalapp.data.model.Report

data class ReportResult(val status: Status, val data: Report?, val message: String?) {
    companion object {
        fun success(data: Report) = ReportResult(SUCCESS, data, null)

        fun error(msg: String) = ReportResult(ERROR, null, msg)

        fun loading() = ReportResult(LOADING, null, null)
    }
}
