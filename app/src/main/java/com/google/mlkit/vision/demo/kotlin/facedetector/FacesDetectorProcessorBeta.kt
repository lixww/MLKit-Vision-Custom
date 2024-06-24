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

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.demo.GraphicOverlay
import com.google.mlkit.vision.demo.kotlin.VisionProcessorBase
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.MatOfRect
import org.opencv.core.Rect
import org.opencv.objdetect.CascadeClassifier
import java.io.File
import java.util.Locale

/** Faces Detector Demo (Beta).  */
class FacesDetectorProcessorBeta(context: Context, detectorOptions: FaceDetectorOptions?) :
    VisionProcessorBase<List<Face>>(context) {

    //  private val detector: FaceDetector
    private lateinit var cascadeClassifier: CascadeClassifier

    private lateinit var facesBeta: MatOfRect


    init {
        val options = detectorOptions
            ?: FaceDetectorOptions.Builder()
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .enableTracking()
                .build()


        if (OpenCVLoader.initLocal()) {

            val classifierFile =
                copyCVModelFromAssets(context, CASCADEFILENAMEINASSSETS, CASCADEFILENAME)

            Log.v(MANUAL_TESTING_LOG, "check classifier file: ${classifierFile.absolutePath}")
            cascadeClassifier = CascadeClassifier(classifierFile.absolutePath)

            facesBeta = MatOfRect()
        } else {
            Log.v(MANUAL_TESTING_LOG, "OpenCVLoader.initLocal() failed")
        }


        //create detector from options
//    detector = FaceDetection.getClient(options)

        Log.v(MANUAL_TESTING_LOG, "Face detector options: $options")
    }

    override fun stop() {
        super.stop()
//    detector.close()
    }

    private fun copyCVModelFromAssets(
        context: Context,
        filenameInAssets: String,
        modelFileName: String
    ): File {
        val inputStream = context.assets.open(filenameInAssets)
        val classifierDir = context.getDir("classifier", Context.MODE_PRIVATE)
        if (!classifierDir.exists())
            classifierDir.mkdirs()
        val classifierFile = File(classifierDir, modelFileName)
        if (!classifierFile.exists())
            classifierFile.createNewFile()
        val outputStream = classifierFile.outputStream()
        outputStream.write(inputStream.readBytes())
        inputStream.close()
        outputStream.close()

        return classifierFile
    }

    private fun createFacesListTask(image: InputImage): Task<List<Face>> {

        val tcs: TaskCompletionSource<List<Face>> = TaskCompletionSource()
        if (false) {
            tcs.setException(RuntimeException("Cooler message"));
        } else {
            tcs.setResult(getListOfFaces(image))
        }

        return tcs.task
    }

    private fun getListOfFaces(image: InputImage): List<Face> {
        //get the real faces info from cascade classifier and return empty
        cascadeClassifier.detectMultiScale(
            inputImageToMat(image),
            facesBeta,
            1.1,
            10
        )

        //just return empty
        return listOf()
    }

    private fun inputImageToMat(image: InputImage): Mat {
        val mat = Mat()
        val bmp = image.bitmapInternal?.copy(Bitmap.Config.ARGB_8888, true)

        Utils.bitmapToMat(bmp, mat)

        return mat
    }

    override fun detectInImage(image: InputImage): Task<List<Face>> {

        return createFacesListTask(image)

        //given an InputImage, return Task with List of Face
//    return detector.process(image)
    }

    override fun onSuccess(faces: List<Face>, graphicOverlay: GraphicOverlay) {
        // handle the empty list here, deal with the real faces info to draw on graphicOverlay
        if (faces.isEmpty()) {
            Log.v(MANUAL_TESTING_LOG, "Use faces from opencv")
            facesBeta.toArray().forEach {
                Log.v(MANUAL_TESTING_LOG, "face: $it")
                graphicOverlay.add(FaceGraphicBeta(graphicOverlay, it))
                logExtrasForTesting(it)
            }

        } else {

            for (face in faces) {
                graphicOverlay.add(FaceGraphic(graphicOverlay, face))
                logExtrasForTesting(face)
            }
        }
    }

    override fun onFailure(e: Exception) {
        Log.e(TAG, "Face detection failed $e")
    }

    companion object {
        private const val TAG = "FaceDetectorProcessor"
        private const val CASCADEFILENAMEINASSSETS =
            "custom_models/haarcascade_frontalface_default.xml"
        private const val CASCADEFILENAME = "haarcascade_frontalface_default.xml"

        private fun logExtrasForTesting(face: Rect) {
            Log.v(
                MANUAL_TESTING_LOG,
                "face top and left: " + face.tl().toString()
            )
            Log.v(
                MANUAL_TESTING_LOG,
                "face bottom and right: " + face.br().toString()
            )
        }

        private fun logExtrasForTesting(face: Face?) {
            if (face != null) {
                Log.v(
                    MANUAL_TESTING_LOG,
                    "face bounding box: " + face.boundingBox.flattenToString()
                )
                Log.v(
                    MANUAL_TESTING_LOG,
                    "face Euler Angle X: " + face.headEulerAngleX
                )
                Log.v(
                    MANUAL_TESTING_LOG,
                    "face Euler Angle Y: " + face.headEulerAngleY
                )
                Log.v(
                    MANUAL_TESTING_LOG,
                    "face Euler Angle Z: " + face.headEulerAngleZ
                )
                // All landmarks
                val landMarkTypes = intArrayOf(
                    FaceLandmark.MOUTH_BOTTOM,
                    FaceLandmark.MOUTH_RIGHT,
                    FaceLandmark.MOUTH_LEFT,
                    FaceLandmark.RIGHT_EYE,
                    FaceLandmark.LEFT_EYE,
                    FaceLandmark.RIGHT_EAR,
                    FaceLandmark.LEFT_EAR,
                    FaceLandmark.RIGHT_CHEEK,
                    FaceLandmark.LEFT_CHEEK,
                    FaceLandmark.NOSE_BASE
                )
                val landMarkTypesStrings = arrayOf(
                    "MOUTH_BOTTOM",
                    "MOUTH_RIGHT",
                    "MOUTH_LEFT",
                    "RIGHT_EYE",
                    "LEFT_EYE",
                    "RIGHT_EAR",
                    "LEFT_EAR",
                    "RIGHT_CHEEK",
                    "LEFT_CHEEK",
                    "NOSE_BASE"
                )
                for (i in landMarkTypes.indices) {
                    val landmark = face.getLandmark(landMarkTypes[i])
                    if (landmark == null) {
                        Log.v(
                            MANUAL_TESTING_LOG,
                            "No landmark of type: " + landMarkTypesStrings[i] + " has been detected"
                        )
                    } else {
                        val landmarkPosition = landmark.position
                        val landmarkPositionStr =
                            String.format(
                                Locale.US,
                                "x: %f , y: %f",
                                landmarkPosition.x,
                                landmarkPosition.y
                            )
                        Log.v(
                            MANUAL_TESTING_LOG,
                            "Position for face landmark: " +
                                    landMarkTypesStrings[i] +
                                    " is :" +
                                    landmarkPositionStr
                        )
                    }
                }
                Log.v(
                    MANUAL_TESTING_LOG,
                    "face left eye open probability: " + face.leftEyeOpenProbability
                )
                Log.v(
                    MANUAL_TESTING_LOG,
                    "face right eye open probability: " + face.rightEyeOpenProbability
                )
                Log.v(
                    MANUAL_TESTING_LOG,
                    "face smiling probability: " + face.smilingProbability
                )
                Log.v(
                    MANUAL_TESTING_LOG,
                    "face tracking id: " + face.trackingId
                )
            }
        }
    }
}
