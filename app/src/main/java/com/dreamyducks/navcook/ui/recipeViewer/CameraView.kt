package com.dreamyducks.navcook.ui.recipeViewer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.dreamyducks.navcook.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraView(
    viewerViewModel: ViewerViewModel,
    onCapture: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var capturedImage by remember { mutableStateOf<Bitmap?>(null) }
    val lifecycleOwner = LocalLifecycleOwner.current

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

    Box(
        modifier = modifier
    ) {
        if (capturedImage == null) {
            CameraScreen(
                context = context,
                lifecycleOwner = lifecycleOwner,
                onCapture = { bitmap ->
                    capturedImage = bitmap
                }
            )
        } else {
            viewerViewModel.updateAskableInput(capturedImage!!)
            onCapture()
            AnswerView(
                viewerViewModel
            )
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
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }//{ ProcessCameraProvider.getInstance(context) }
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
                        scaleType = PreviewView.ScaleType.FILL_CENTER
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
                Log.d("MainActivity", "Error on capture image")
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AnswerView(
    viewModel: ViewerViewModel,
    modifier: Modifier = Modifier
) {
    val viewerUiState = viewModel.viewerUiState.collectAsState()
    if(viewerUiState.value.askableRes.isEmpty()) { //show loading
        Box(
            modifier = modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularWavyProgressIndicator(
            )
        }
    } else { //answer available
        Column(
            modifier = modifier
                .padding(vertical = dimensionResource(R.dimen.padding_medium))
        ) {
            Text(
                viewerUiState.value.askableRes
            )
        }
    }
}

@Composable
private fun CaptureImageView(
    bitmap: Bitmap,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = modifier.fillMaxSize(),
            contentScale = ContentScale.Fit,
        )
    }
}