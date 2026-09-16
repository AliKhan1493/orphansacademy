package com.example.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.example.model.ExamTest
import java.io.File
import java.io.FileOutputStream

object PdfGenerator {
    const val PAGE_WIDTH = 595 // Standard A4 width in PostScript points (72 dpi)
    const val PAGE_HEIGHT = 842 // Standard A4 height in PostScript points (72 dpi)

    /**
     * Generates a printable A4 Examination PDF document for an ExamTest.
     * Returns the generated File in the app's cache directory.
     */
    fun generateExamPdf(context: Context, test: ExamTest, teacherName: String): File {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        // Paint setup
        val paint = Paint().apply { isAntiAlias = true }

        // Background
        paint.color = Color.WHITE
        paint.style = Paint.Style.FILL
        canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), PAGE_HEIGHT.toFloat(), paint)

        // Outer Border
        paint.color = Color.rgb(30, 58, 138) // Academy Navy
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2.5f
        canvas.drawRect(25f, 25f, (PAGE_WIDTH - 25).toFloat(), (PAGE_HEIGHT - 25).toFloat(), paint)

        // Inner Border
        paint.color = Color.rgb(217, 119, 6) // Gold accent
        paint.strokeWidth = 1f
        canvas.drawRect(29f, 29f, (PAGE_WIDTH - 29).toFloat(), (PAGE_HEIGHT - 29).toFloat(), paint)

        // Header
        paint.style = Paint.Style.FILL
        paint.color = Color.rgb(15, 23, 42)
        paint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        paint.textSize = 18f
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("ORPHAN'S ACADEMY", PAGE_WIDTH / 2f, 65f, paint)

        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        paint.textSize = 10f
        paint.color = Color.rgb(71, 85, 105)
        canvas.drawText("Official Academic Evaluation & Assessment Protocol • ISO-Certified", PAGE_WIDTH / 2f, 80f, paint)

        // Divider line
        paint.strokeWidth = 1.2f
        paint.color = Color.rgb(30, 58, 138)
        canvas.drawLine(40f, 92f, (PAGE_WIDTH - 40).toFloat(), 92f, paint)

        // Title box
        paint.style = Paint.Style.FILL
        paint.color = Color.rgb(241, 245, 249)
        canvas.drawRect(40f, 100f, (PAGE_WIDTH - 40).toFloat(), 138f, paint)

        paint.color = Color.rgb(15, 23, 42)
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        paint.textSize = 13f
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("EXAMINATION PAPER: ${test.title.uppercase()}", PAGE_WIDTH / 2f, 122f, paint)

        // Exam Metadata Grid
        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 9.5f
        paint.color = Color.rgb(30, 41, 59)
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)

        val metaY1 = 158f
        val metaY2 = 175f

        canvas.drawText("Subject: ${test.subject}", 45f, metaY1, paint)
        canvas.drawText("Grade: ${test.gradeLevel}", 230f, metaY1, paint)
        canvas.drawText("Max Marks: ${test.totalMarks}", 410f, metaY1, paint)

        canvas.drawText("Date: ${test.examDate}", 45f, metaY2, paint)
        canvas.drawText("Duration: ${test.durationMinutes} mins", 230f, metaY2, paint)
        canvas.drawText("Faculty: $teacherName", 410f, metaY2, paint)

        // Student Info Fill-in Fields
        paint.style = Paint.Style.STROKE
        paint.color = Color.rgb(203, 213, 225)
        paint.strokeWidth = 1f
        canvas.drawRect(40f, 190f, (PAGE_WIDTH - 40).toFloat(), 230f, paint)

        paint.style = Paint.Style.FILL
        paint.color = Color.rgb(71, 85, 105)
        paint.textSize = 9f
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        canvas.drawText("Candidate Name: ___________________________________", 48f, 210f, paint)
        canvas.drawText("Admission ID: ______________", 330f, 210f, paint)
        canvas.drawText("Room / Desk: _______", 460f, 210f, paint)

        // Instructions
        paint.color = Color.rgb(180, 83, 9)
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        paint.textSize = 9.5f
        canvas.drawText("INSTRUCTIONS TO CANDIDATES:", 40f, 248f, paint)

        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        paint.color = Color.rgb(51, 65, 85)
        paint.textSize = 8.5f
        val instructionsText = if (test.instructions.isNotBlank()) test.instructions else
            "1. Answer all questions clearly. 2. Write your Admission No on every page. 3. Calculators/devices strictly prohibited."
        canvas.drawText(instructionsText, 40f, 262f, paint)

        // Questions Section Divider
        paint.color = Color.rgb(30, 58, 138)
        paint.strokeWidth = 1f
        canvas.drawLine(40f, 275f, (PAGE_WIDTH - 40).toFloat(), 275f, paint)

        // Questions
        val questionsList = test.questionsJson.split("\n")
            .filter { it.isNotBlank() }
            .ifEmpty {
                listOf(
                    "Define the primary theoretical foundations associated with this topic. (10 Marks)",
                    "Explain the algorithmic and structural differences with a suitable diagram. (15 Marks)",
                    "Provide a comprehensive case study solution for the given scenario. (15 Marks)",
                    "Short Notes: A) Methodology B) Empirical Observations C) Error Handling (10 Marks)"
                )
            }

        var currentY = 300f
        paint.color = Color.rgb(15, 23, 42)

        for ((index, q) in questionsList.withIndex()) {
            if (currentY > PAGE_HEIGHT - 120) break

            paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            paint.textSize = 9.5f
            canvas.drawText("Q${index + 1}.", 42f, currentY, paint)

            paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            canvas.drawText(q.trim(), 65f, currentY, paint)

            // Draw lined answer area
            paint.color = Color.rgb(226, 232, 240)
            paint.strokeWidth = 0.8f
            canvas.drawLine(65f, currentY + 22f, (PAGE_WIDTH - 45).toFloat(), currentY + 22f, paint)
            canvas.drawLine(65f, currentY + 44f, (PAGE_WIDTH - 45).toFloat(), currentY + 44f, paint)
            canvas.drawLine(65f, currentY + 66f, (PAGE_WIDTH - 45).toFloat(), currentY + 66f, paint)

            paint.color = Color.rgb(15, 23, 42)
            currentY += 88f
        }

        // Footer / Signature
        val footerY = (PAGE_HEIGHT - 55).toFloat()
        paint.color = Color.rgb(148, 163, 184)
        paint.strokeWidth = 0.8f
        canvas.drawLine(40f, footerY - 15f, (PAGE_WIDTH - 40).toFloat(), footerY - 15f, paint)

        paint.style = Paint.Style.FILL
        paint.color = Color.rgb(100, 116, 139)
        paint.textSize = 8.5f
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("Printed from Orphan's Academy Assessment Module", 40f, footerY, paint)

        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("Examiner Signature: __________________", (PAGE_WIDTH - 40).toFloat(), footerY, paint)

        pdfDocument.finishPage(page)

        // Write to cache file
        val outputFile = File(context.cacheDir, "exam_${test.id}.pdf")
        FileOutputStream(outputFile).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()

        return outputFile
    }
}
