/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.mlkit.vision.demo.kotlin.facedetector

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.google.mlkit.vision.demo.GraphicOverlay
import com.google.mlkit.vision.demo.GraphicOverlay.Graphic
import org.opencv.core.Rect
import kotlin.random.Random

/**
 * Graphic instance for rendering face position, contour, and landmarks within the associated
 * graphic overlay view.
 */
class FaceGraphicBeta(overlay: GraphicOverlay?, private val face: Rect) : Graphic(overlay) {
    private val facePositionPaint: Paint
    private val numColors = COLORS.size
    private val idPaints = Array(numColors) { Paint() }
    private val boxPaints = Array(numColors) { Paint() }
    private val labelPaints = Array(numColors) { Paint() }

    init {
        val selectedColor = Color.WHITE
        facePositionPaint = Paint()
        facePositionPaint.color = selectedColor
        for (i in 0 until numColors) {
            idPaints[i] = Paint()
            idPaints[i].color = COLORS[i][0]
            idPaints[i].textSize = ID_TEXT_SIZE
            boxPaints[i] = Paint()
            boxPaints[i].color = COLORS[i][1]
            boxPaints[i].style = Paint.Style.STROKE
            boxPaints[i].strokeWidth = BOX_STROKE_WIDTH
            labelPaints[i] = Paint()
            labelPaints[i].color = COLORS[i][1]
            labelPaints[i].style = Paint.Style.FILL
        }
    }

    /** Draws the face annotations for position on the supplied canvas. */
    override fun draw(canvas: Canvas) {
        // Draws a circle at the position of the detected face, with the face's track id below.

        // Draws a circle at the position of the detected face, with the face's track id below.

        val x = translateX((face.x + face.width / 2).toFloat())
        val y = translateY((face.y + face.height / 2).toFloat())
        canvas.drawCircle(x, y, FACE_POSITION_RADIUS, facePositionPaint)

        // Calculate positions.
        val left = face.tl().x.toFloat()
        val top = face.tl().y.toFloat()
        val right = face.br().x.toFloat()
        val bottom = face.br().y.toFloat()

        // Decide color based on face ID
        val colorID = Random.nextInt(0, NUM_COLORS)

        canvas.drawRect(left, top, right, bottom, boxPaints[colorID])

//
//        // Draws all face contours.
//        for (contour in face.allContours) {
//            for (point in contour.points) {
//                canvas.drawCircle(
//                    translateX(point.x),
//                    translateY(point.y),
//                    FACE_POSITION_RADIUS,
//                    facePositionPaint
//                )
//            }
//        }


//        // Draw facial landmarks
//        drawFaceLandmark(canvas, FaceLandmark.LEFT_EYE)
//        drawFaceLandmark(canvas, FaceLandmark.RIGHT_EYE)
//        drawFaceLandmark(canvas, FaceLandmark.LEFT_CHEEK)
//        drawFaceLandmark(canvas, FaceLandmark.RIGHT_CHEEK)
    }

//    private fun drawFaceLandmark(canvas: Canvas, @LandmarkType landmarkType: Int) {
//        val faceLandmark = face.getLandmark(landmarkType)
//        if (faceLandmark != null) {
//            canvas.drawCircle(
//                translateX(faceLandmark.position.x),
//                translateY(faceLandmark.position.y),
//                FACE_POSITION_RADIUS,
//                facePositionPaint
//            )
//        }
//    }

    companion object {
        private const val FACE_POSITION_RADIUS = 8.0f
        private const val ID_TEXT_SIZE = 30.0f
        private const val ID_Y_OFFSET = 40.0f
        private const val BOX_STROKE_WIDTH = 5.0f
        private const val NUM_COLORS = 10
        private val COLORS =
            arrayOf(
                intArrayOf(Color.BLACK, Color.WHITE),
                intArrayOf(Color.WHITE, Color.MAGENTA),
                intArrayOf(Color.BLACK, Color.LTGRAY),
                intArrayOf(Color.WHITE, Color.RED),
                intArrayOf(Color.WHITE, Color.BLUE),
                intArrayOf(Color.WHITE, Color.DKGRAY),
                intArrayOf(Color.BLACK, Color.CYAN),
                intArrayOf(Color.BLACK, Color.YELLOW),
                intArrayOf(Color.WHITE, Color.BLACK),
                intArrayOf(Color.BLACK, Color.GREEN)
            )
    }
}
