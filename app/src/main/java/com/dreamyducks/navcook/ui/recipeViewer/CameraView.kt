package com.dreamyducks.navcook.ui.recipeViewer

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.dreamyducks.navcook.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraView(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraPermissionGranted = rememberPermissionState(Manifest.permission.CAMERA)

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "Camera permission needed", Toast.LENGTH_SHORT).show()
        }
    }

    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val backCallback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                isEnabled = false
            }
        }
    }

    DisposableEffect(onBackPressedDispatcher, backCallback) {
        onBackPressedDispatcher?.addCallback(backCallback)
        onDispose {
            backCallback.remove()
        }
    }

    LaunchedEffect(Unit) {
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    Box(
        modifier = modifier
            .fillMaxHeight(0.5f)
    ) {
        if (cameraPermissionGranted.status.isGranted) {
            CameraScreen(
                context = context,
                lifecycleOwner = lifecycleOwner,
                onCapture = {}
            )
        } else {
            Button(
                onClick = { requestPermissionLauncher.launch(Manifest.permission.CAMERA) },
                modifier = modifier
                    .align(Alignment.Center)
            ) {
                Text("Allow camera access")
            }
        }
    }
}

@Composable
private fun CameraScreen(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    onCapture: (Bitmap) -> Unit,
    modifier: Modifier = Modifier
) {
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    var preview by remember { mutableStateOf<androidx.camera.core.Preview?>(null) }

    var captureClicked by remember { mutableStateOf(true) }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        if (preview != null) {
            AndroidView(
                factory = {
                    PreviewView(it).apply {
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                        scaleType = PreviewView.ScaleType.FIT_CENTER
                    }
                },
                update = { previewView ->
                    preview?.setSurfaceProvider(previewView.surfaceProvider)
                },
                modifier = modifier.fillMaxSize()
            )
        }
        Button(
            onClick = {
                captureClicked = false
                imageCapture?.let {
                    captureImage(
                        it,
                        cameraExecutor,
                        onImageCapture = { bitmap -> onCapture(bitmap) }
                    )
                }
            },
            enabled = captureClicked,
            modifier = modifier
                .align(Alignment.BottomCenter)
                .padding(dimensionResource(R.dimen.padding_medium))
        ) {
            Text("Take Picture")
        }
    }

    LaunchedEffect(Unit) {
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            preview = androidx.camera.core.Preview.Builder().build()
            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                val useCaseGroup = UseCaseGroup.Builder()
                    .addUseCase(preview!!)
                    .addUseCase(imageCapture!!)
                    .build()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    useCaseGroup
                )
                preview?.setSurfaceProvider(
                    PreviewView(context).surfaceProvider
                )
            } catch (exe: Exception) {
                Log.e("CameraX", "Use case binding failed", exe)
            }
        }, ContextCompat.getMainExecutor(context))
    }
    DisposableEffect(cameraExecutor) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }
}

private fun captureImage(
    imageCapture: ImageCapture,
    executors: ExecutorService,
    onImageCapture: (Bitmap) -> Unit
) {
    imageCapture.takePicture(
        executors,
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                // Handle error appropriately
            }

            override fun onCaptureSuccess(image: androidx.camera.core.ImageProxy) {
                val buffer = image.planes[0].buffer
                val bytes = ByteArray(buffer.remaining())
                buffer.get(bytes)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                image.close()
                onImageCapture(bitmap)
            }
        }
    )
}