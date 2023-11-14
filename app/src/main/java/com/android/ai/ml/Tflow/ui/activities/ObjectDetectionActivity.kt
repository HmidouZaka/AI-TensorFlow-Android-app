package com.android.ai.ml.Tflow.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Spinner
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.core.view.WindowCompat
import com.android.ai.ml.Tflow.R
import com.android.ai.ml.Tflow.ui.theme.AIAndroidAppTheme
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabel
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.delay
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects

class ObjectDetectionActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            AIAndroidAppTheme {

                var imageBitmap: Bitmap? by remember {
                    mutableStateOf(null)
                }
                val topAppBarBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
                val file = remember {
                    createImageFile()
                }
                val uri = remember {
                    FileProvider.getUriForFile(
                        Objects.requireNonNull(this),
                        "$packageName.provider", file
                    )
                }
                val requestToCamera = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.TakePicture(),
                    onResult = {
                        if (it) {
                            val image = uri.toBitmap()
                            imageBitmap = image
                        }
                    }
                )

                val requestToGallery = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent(),
                    onResult = {
                        if (it != null) {
                            val image = it.toBitmap()
                            imageBitmap = image
                        }
                    }
                )
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(topAppBarBehavior.nestedScrollConnection),
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(text = "ML Scanner")
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = { finish() }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = ""
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TopAppBarDefaults.largeTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                scrolledContainerColor = MaterialTheme.colorScheme.secondary
                            ),
                            scrollBehavior = topAppBarBehavior,
                            actions = imageBitmap?.let {
                                {
                                    IconButton(
                                        onClick = { imageBitmap = null }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = ""
                                        )
                                    }
                                }
                            } ?: {}

                        )
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it),
                    ) {
                        if (imageBitmap == null) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp),
                                    onClick = {
                                        requestToCamera.launch(uri)
                                    },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.secondary
                                    )
                                ) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.camera_icon),
                                            contentDescription = null,
                                            modifier = Modifier.size(110.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "From Camera",
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.W400,
                                        fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                }

                                Spacer(modifier = Modifier.padding(12.dp))

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp),
                                    onClick = {
                                        requestToGallery.launch("image/*")
                                    },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.secondary
                                    )
                                ) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.image_icon),
                                            contentDescription = null,
                                            modifier = Modifier.size(110.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "From Gallery",
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.W400,
                                        fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                            }
                        } else {

                            var result by remember {
                                mutableStateOf<List<ImageLabel>?>(null)
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(
                                        rememberScrollState()
                                    )
                            ) {
                                Spacer(modifier = Modifier.height(35.dp))
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    imageBitmap?.asImageBitmap()?.let { it1 ->
                                        Image(
                                            bitmap = it1,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(220.dp)
                                                .clip(RoundedCornerShape(14.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }

                                result?.let { it ->
                                    Column(
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        for (imageLabel in it) {


                                            Spacer(modifier = Modifier.height(8.dp))
                                            Spinner(
                                                confidence = imageLabel.confidence,
                                                text = imageLabel.text,
                                            )

                                        }

                                    }
                                } ?: Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No Result",
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.W400,
                                        fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }

                            LaunchedEffect(key1 = true) {
                                imageBitmap?.let {
                                    scanImage(it, 0) {
                                        result = it
                                    }
                                }
                            }
                        }


                    }
                }

            }
        }
    }


    @SuppressLint("Recycle")
    fun Uri.toBitmap(): Bitmap? {
        return try {
            val inputStream = contentResolver.openInputStream(this)
            BitmapFactory.decodeStream(inputStream)
        } catch (ex: Exception) {
            null
        }
    }


    fun Context.createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        return File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            externalCacheDir /* directory */
        )
    }


    fun scanImage(bitmap: Bitmap, rotation: Int, onResult: (List<ImageLabel>?) -> Unit) {
        try {
            val imageOptions = ImageLabelerOptions.Builder()
                .setConfidenceThreshold(0.5f)
                .build()

            val imageLabeling = ImageLabeling.getClient(imageOptions)
            val inputImage = InputImage.fromBitmap(bitmap, rotation)
            val process = imageLabeling.process(inputImage)
            process.addOnSuccessListener {
                if (it.isEmpty()) {
                    onResult(null)
                } else {
                    onResult(it)
                }
            }.addOnFailureListener {
                onResult(null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            onResult(null)
        }
    }


    @Composable
    fun Spinner(
        confidence: Float,
        text: String,
    ) {

        var isStarted by remember {
            mutableStateOf(false)
        }
        Box {

            val animateTextColor by animateColorAsState(
                label = "color",
                targetValue = when (isStarted) {
                    false -> MaterialTheme.colorScheme.onBackground
                    true ->  MaterialTheme.colorScheme.background
                },
                animationSpec = tween(1000)
            )

            val animateColor by animateColorAsState(
                label = "color",
                targetValue = when (isStarted) {
                    false -> MaterialTheme.colorScheme.primary
                    true -> {
                        if (confidence in 0.5f..0.6f) {
                            Color(0xFFFF5656)
                        } else if (confidence in 0.6f..0.7f) {
                            Color(0xFFFF6D00)
                        } else if (confidence in 0.7f..0.8f) {
                            Color(0xFF0091EA)
                        } else if (confidence in 0.8f..0.9f) {
                            Color(0xFF6200EA)
                        } else if (confidence in 0.9f..1f) {
                            Color(0xFF17C70B)
                        } else {
                            Color(0xFF000000)
                        }
                    }
                },
                animationSpec = tween(1000)
            )
            val animatePosition by animateFloatAsState(
                label = "position",
                targetValue = if (isStarted) confidence else 0f,
                animationSpec = tween(1000)
            )


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFEBEBEB))
                    .drawBehind {
                        drawRect(
                            color = animateColor,
                            topLeft = Offset.Zero,
                            size = Size(
                                size.width *animatePosition,
                                size.height
                            ),
                        )
                    }
            ) {
                Text(
                    text = "${text} : ${animatePosition}",
                    color = animateTextColor,
                    fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                    fontSize = 18.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }


        LaunchedEffect(key1 = true ){
            delay(100)
            isStarted = true
        }
    }
}
