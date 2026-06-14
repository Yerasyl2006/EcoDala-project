package com.ecodala.feature.map.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Paint
import android.graphics.Path as AndroidPath
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.ElectricScooter
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.PedalBike
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.WineBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecodala.core.domain.model.RecyclingPoint
import com.ecodala.core.domain.model.WasteType
import com.ecodala.core.localization.LocalEcoStrings
import com.ecodala.core.ui.theme.EcoDalaTheme
import com.ecodala.core.ui.theme.EcoGreen
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt
import java.net.URL
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

@Composable
fun MapRoute(
    onPointDetailsClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    MapScreen(
        uiState = uiState,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onWasteTypeSelected = viewModel::onWasteTypeSelected,
        onPointSelected = viewModel::onPointSelected,
        onRouteClick = viewModel::onRouteRequested,
        onPointClick = { onPointDetailsClick(it.id) },
        modifier = modifier
    )
}

@Composable
fun MapScreen(
    uiState: MapUiState,
    onSearchQueryChange: (String) -> Unit,
    onWasteTypeSelected: (WasteType?) -> Unit,
    onPointSelected: (RecyclingPoint) -> Unit,
    onRouteClick: (RecyclingPoint) -> Unit,
    onPointClick: (RecyclingPoint) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasLocationPermission by remember {
        mutableStateOf(context.hasLocationPermission())
    }
    var deviceLocationEnabled by remember {
        mutableStateOf(context.isDeviceLocationEnabled())
    }
    var userLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var locationServiceDisabled by remember {
        mutableStateOf(hasLocationPermission && !deviceLocationEnabled)
    }
    val initialPoint = uiState.selectedPoint ?: uiState.filteredPoints.firstOrNull()
    val initialGeoPoint = initialPoint?.toGeoPoint() ?: GeoPoint(43.238949, 76.889709)
    var mapView by remember { mutableStateOf<MapView?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var isPreviewExpanded by remember { mutableStateOf(false) }
    var selectedRouteMode by remember { mutableStateOf(RouteMode.Walk) }
    var streetRoute by remember { mutableStateOf<StreetRouteResult?>(null) }
    var routeLoading by remember { mutableStateOf(false) }
    var routeError by remember { mutableStateOf<String?>(null) }
    val fusedLocationClient = remember(context) {
        LocationServices.getFusedLocationProviderClient(context)
    }
    val locationPermissionPrefs = remember(context) {
        context.getSharedPreferences("map_permission", Context.MODE_PRIVATE)
    }
    var showLocationPermissionPrompt by remember {
        mutableStateOf(
            !hasLocationPermission &&
                locationPermissionPrefs.getString(LOCATION_CHOICE_KEY, null) != LOCATION_CHOICE_NEVER
        )
    }

    fun updateUserLocation(location: Location?) {
        location ?: return
        val target = GeoPoint(location.latitude, location.longitude)
        userLocation = target
        coroutineScope.launch {
            mapView?.controller?.setZoom(16.0)
            mapView?.controller?.animateTo(target)
        }
    }

    @SuppressLint("MissingPermission")
    fun requestSingleLocationUpdate() {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1_000L)
            .setMaxUpdates(1)
            .build()
        lateinit var callback: LocationCallback
        callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                fusedLocationClient.removeLocationUpdates(callback)
                updateUserLocation(result.lastLocation)
            }
        }

        fusedLocationClient.requestLocationUpdates(request, callback, Looper.getMainLooper())
    }

    @SuppressLint("MissingPermission")
    fun loadUserLocation() {
        if (!context.hasLocationPermission()) return
        if (!context.isDeviceLocationEnabled()) {
            locationServiceDisabled = true
            return
        }

        locationServiceDisabled = false

        val cancellationTokenSource = CancellationTokenSource()
        fusedLocationClient
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token)
            .addOnSuccessListener { location ->
                if (location != null) {
                    updateUserLocation(location)
                } else {
                    requestSingleLocationUpdate()
                }
            }
            .addOnFailureListener {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            updateUserLocation(location)
                        } else {
                            requestSingleLocationUpdate()
                        }
                    }
            }
    }
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (hasLocationPermission) {
            loadUserLocation()
        }
    }

    fun requestOrMoveToUserLocation() {
        deviceLocationEnabled = context.isDeviceLocationEnabled()
        if (hasLocationPermission) {
            loadUserLocation()
        } else {
            showLocationPermissionPrompt = true
        }
    }

    LaunchedEffect(Unit) {
        if (hasLocationPermission) {
            loadUserLocation()
        }
    }

    DisposableEffect(lifecycleOwner, hasLocationPermission) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                deviceLocationEnabled = context.isDeviceLocationEnabled()
                if (hasLocationPermission && deviceLocationEnabled) {
                    locationServiceDisabled = false
                    loadUserLocation()
                } else if (hasLocationPermission) {
                    locationServiceDisabled = true
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(uiState.selectedPoint?.id) {
        isPreviewExpanded = false
        uiState.selectedPoint?.let { point ->
            mapView?.controller?.setZoom(14.5)
            mapView?.controller?.animateTo(point.toGeoPoint())
        }
    }

    LaunchedEffect(uiState.routeDestination?.id) {
        uiState.routeDestination?.let { point ->
            mapView?.controller?.setZoom(13.4)
            mapView?.controller?.animateTo(point.toGeoPoint())
        }
    }

    LaunchedEffect(
        uiState.routeDestination?.id,
        userLocation?.latitude,
        userLocation?.longitude,
        selectedRouteMode
    ) {
        val start = userLocation
        val destination = uiState.routeDestination
        if (start == null || destination == null) {
            streetRoute = null
            routeLoading = false
            routeError = null
            return@LaunchedEffect
        }

        routeLoading = true
        routeError = null
        streetRoute = runCatching {
            fetchStreetRoute(start, destination.toGeoPoint(), selectedRouteMode)
        }.onFailure {
            routeError = "Street route unavailable, showing approximate route"
        }.getOrNull()
        routeLoading = false
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        OpenStreetMapView(
            modifier = Modifier.fillMaxSize(),
            initialCenter = initialGeoPoint,
            points = uiState.filteredPoints,
            selectedPoint = uiState.selectedPoint,
            routeDestination = uiState.routeDestination,
            userLocation = userLocation,
            streetRoutePoints = streetRoute?.points.orEmpty(),
            onMapReady = { mapView = it },
            onPointSelected = onPointSelected
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 22.dp)
                .padding(top = 24.dp)
        ) {
            MapSearchBar(
                query = uiState.searchQuery,
                onQueryChange = onSearchQueryChange
            )

            Spacer(modifier = Modifier.height(14.dp))

            WasteFilterRow(
                selectedWasteType = uiState.selectedWasteType,
                onWasteTypeSelected = onWasteTypeSelected
            )

            if (locationServiceDisabled) {
                Spacer(modifier = Modifier.height(12.dp))
                LocationServiceHint(
                    onEnableClick = {
                        context.openLocationSettings()
                    }
                )
            }
        }

        if (uiState.filteredPoints.isEmpty()) {
            EmptyMapResult(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 28.dp)
            )
        }

        if (
            showLocationPermissionPrompt &&
            !hasLocationPermission
        ) {
            if (!deviceLocationEnabled) {
                LocationEnablePromptCard(
                    onTurnOnClick = {
                        context.openLocationSettings()
                    },
                    onNeverClick = {
                        locationPermissionPrefs.edit()
                            .putString(LOCATION_CHOICE_KEY, LOCATION_CHOICE_NEVER)
                            .apply()
                        showLocationPermissionPrompt = false
                    },
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 24.dp)
                )
            } else {
                LocationChoicePromptCard(
                    onAlwaysClick = {
                        locationPermissionPrefs.edit()
                            .putString(LOCATION_CHOICE_KEY, LOCATION_CHOICE_ALWAYS)
                            .apply()
                        showLocationPermissionPrompt = false
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    },
                    onOneTimeClick = {
                        showLocationPermissionPrompt = false
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    },
                    onNeverClick = {
                        locationPermissionPrefs.edit()
                            .putString(LOCATION_CHOICE_KEY, LOCATION_CHOICE_NEVER)
                            .apply()
                        showLocationPermissionPrompt = false
                    },
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 24.dp)
                )
            }
        }

        MapFloatingControls(
            onMyLocationClick = ::requestOrMoveToUserLocation,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = 18.dp,
                    bottom = if (uiState.selectedPoint != null) {
                        if (isPreviewExpanded) 316.dp else 162.dp
                    } else {
                        92.dp
                    }
                )
        )

        uiState.selectedPoint?.let { point ->
            RecyclingPointPreviewCard(
                point = point,
                onClick = { onPointClick(point) },
                onRouteClick = {
                    if (userLocation == null) {
                        requestOrMoveToUserLocation()
                    } else {
                        onRouteClick(point)
                        isPreviewExpanded = true
                    }
                },
                expanded = isPreviewExpanded,
                onExpandedChange = { isPreviewExpanded = it },
                userLocation = userLocation,
                routeActive = uiState.routeDestination?.id == point.id,
                selectedRouteMode = selectedRouteMode,
                onRouteModeSelected = { selectedRouteMode = it },
                streetRoute = streetRoute,
                routeLoading = routeLoading,
                routeError = routeError,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 0.dp)
            )
        }
    }
}

@Composable
private fun OpenStreetMapView(
    initialCenter: GeoPoint,
    points: List<RecyclingPoint>,
    selectedPoint: RecyclingPoint?,
    routeDestination: RecyclingPoint?,
    userLocation: GeoPoint?,
    streetRoutePoints: List<GeoPoint>,
    onMapReady: (MapView) -> Unit,
    onPointSelected: (RecyclingPoint) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    AndroidView(
        modifier = modifier,
        factory = {
            Configuration.getInstance().userAgentValue = context.packageName

            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                minZoomLevel = 4.0
                maxZoomLevel = 19.0
                controller.setZoom(13.5)
                controller.setCenter(initialCenter)
                onMapReady(this)
            }
        },
        update = { map ->
            map.overlays.clear()

            routeDestination?.let { destination ->
                userLocation?.let { start ->
                    map.overlays.add(
                        Polyline().apply {
                            setPoints(
                                streetRoutePoints.ifEmpty {
                                    buildRoutePreviewPoints(start, destination)
                                }
                            )
                            outlinePaint.color = android.graphics.Color.rgb(18, 129, 54)
                            outlinePaint.strokeWidth = 11f
                        }
                    )
                }
            }

            userLocation?.let { location ->
                map.overlays.add(
                    Marker(map).apply {
                        position = location
                        title = "You are here"
                        icon = createUserLocationMarkerIcon(context)
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                    }
                )
            }

            points.forEach { point ->
                val isSelected = selectedPoint?.id == point.id
                map.overlays.add(
                    Marker(map).apply {
                        position = point.toGeoPoint()
                        title = point.name
                        snippet = point.address
                        icon = createWasteMarkerIcon(
                            context = context,
                            wasteType = point.acceptedWasteTypes.firstOrNull() ?: WasteType.Plastic,
                            selected = isSelected
                        )
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        alpha = if (isSelected) 1f else 0.86f
                        setOnMarkerClickListener { _, _ ->
                            onPointSelected(point)
                            true
                        }
                    }
                )
            }

            map.invalidate()
        }
    )
}

@Composable
private fun MapFloatingControls(
    onMyLocationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            modifier = Modifier
                .size(50.dp)
                .clickable(onClick = onMyLocationClick),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.MyLocation,
                    contentDescription = "My location",
                    tint = EcoGreen,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun LocationEnablePromptCard(
    onTurnOnClick: () -> Unit,
    onNeverClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(Color(0xFFE8F5E9), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = EcoGreen,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Turn on phone location",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "First enable location on your phone. Then EcoDala will ask permission and show your exact position on the map.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            LocationPromptButton(
                label = "Turn on phone location",
                containerColor = EcoGreen,
                contentColor = Color.White,
                onClick = onTurnOnClick
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Never",
                modifier = Modifier.clickable(onClick = onNeverClick),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun LocationChoicePromptCard(
    onAlwaysClick: () -> Unit,
    onOneTimeClick: () -> Unit,
    onNeverClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(Color(0xFFE3F2FD), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.MyLocation,
                    contentDescription = null,
                    tint = Color(0xFF2196F3),
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Location access",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Choose how EcoDala can use your location on the map. After your choice, Android will ask for system permission.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            LocationPromptButton(
                label = "Always",
                containerColor = EcoGreen,
                contentColor = Color.White,
                onClick = onAlwaysClick
            )

            Spacer(modifier = Modifier.height(10.dp))

            LocationPromptButton(
                label = "One time",
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface,
                onClick = onOneTimeClick
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Never",
                modifier = Modifier.clickable(onClick = onNeverClick),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun LocationPermissionHint(onRequestClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onRequestClick)
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.LocationOn,
            contentDescription = null,
            tint = EcoGreen,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.size(10.dp))
        Text(
            text = "Enable location to show nearby recycling points",
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun LocationPermissionPromptCard(
    onAllowClick: () -> Unit,
    onLaterClick: () -> Unit,
    onNeverClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(Color(0xFFE3F2FD), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.MyLocation,
                    contentDescription = null,
                    tint = Color(0xFF2196F3),
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Геолокацияны қосамыз ба?",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "EcoDala картада сенің орныңды көрсетіп, жақын recycling point-терді табуға көмектеседі.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            LocationPromptButton(
                label = "Рұқсат беру",
                containerColor = EcoGreen,
                contentColor = Color.White,
                onClick = onAllowClick
            )

            Spacer(modifier = Modifier.height(10.dp))

            LocationPromptButton(
                label = "Кейін",
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface,
                onClick = onLaterClick
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Геолокациясыз жалғастыру",
                modifier = Modifier.clickable(onClick = onNeverClick),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun LocationPromptButton(
    label: String,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(containerColor)
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = contentColor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun LocationServiceHint(onEnableClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onEnableClick)
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.MyLocation,
            contentDescription = null,
            tint = Color(0xFF2196F3),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.size(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Turn on phone location",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Enable GPS to show your exact position",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun MapSearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = null,
            tint = Color(0xFF6C756E),
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.size(14.dp))
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            singleLine = true,
            textStyle = TextStyle(
                color = Color(0xFF243028),
                fontSize = 15.sp
            ),
            decorationBox = { innerTextField ->
                if (query.isEmpty()) {
                    Text(
                        text = LocalEcoStrings.current.searchLocation,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 15.sp
                    )
                }
                innerTextField()
            }
        )
        Icon(
            imageVector = Icons.Filled.FilterList,
            contentDescription = "Filters",
            tint = EcoGreen,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun EmptyMapResult(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = null,
            tint = EcoGreen,
            modifier = Modifier.size(30.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "No recycling points found",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Try another waste type or search query",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun WasteFilterRow(
    selectedWasteType: WasteType?,
    onWasteTypeSelected: (WasteType?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        val strings = LocalEcoStrings.current
        FilterChip(
            label = strings.all,
            icon = Icons.Filled.Recycling,
            selected = selectedWasteType == null,
            colors = FilterChipColors(
                selectedContainer = EcoGreen,
                unselectedContainer = Color.White,
                selectedContent = Color.White,
                unselectedContent = EcoGreen
            ),
            onClick = { onWasteTypeSelected(null) }
        )
        FilterChip(
            label = strings.wasteTypeName(WasteType.Plastic),
            icon = Icons.Filled.LocalDrink,
            selected = selectedWasteType == WasteType.Plastic,
            colors = WasteType.Plastic.filterChipColors(),
            onClick = { onWasteTypeSelected(WasteType.Plastic) }
        )
        FilterChip(
            label = strings.wasteTypeName(WasteType.Glass),
            icon = Icons.Filled.WineBar,
            selected = selectedWasteType == WasteType.Glass,
            colors = WasteType.Glass.filterChipColors(),
            onClick = { onWasteTypeSelected(WasteType.Glass) }
        )
        FilterChip(
            label = strings.wasteTypeName(WasteType.Paper),
            icon = Icons.Filled.InsertDriveFile,
            selected = selectedWasteType == WasteType.Paper,
            colors = WasteType.Paper.filterChipColors(),
            onClick = { onWasteTypeSelected(WasteType.Paper) }
        )
        FilterChip(
            label = strings.wasteTypeName(WasteType.Batteries),
            icon = Icons.Filled.Recycling,
            selected = selectedWasteType == WasteType.Batteries,
            colors = WasteType.Batteries.filterChipColors(),
            onClick = { onWasteTypeSelected(WasteType.Batteries) }
        )
    }
}

@Composable
private fun FilterChip(
    label: String,
    icon: ImageVector,
    selected: Boolean = false,
    colors: FilterChipColors,
    onClick: () -> Unit
) {
    val containerColor = if (selected) colors.selectedContainer else colors.unselectedContainer
    val contentColor = if (selected) colors.selectedContent else colors.unselectedContent

    Row(
        modifier = Modifier
            .height(38.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(containerColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.size(6.dp))
        Text(
            text = label,
            color = contentColor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

private data class FilterChipColors(
    val selectedContainer: Color,
    val unselectedContainer: Color,
    val selectedContent: Color,
    val unselectedContent: Color
)

private fun WasteType.filterChipColors(): FilterChipColors {
    return when (this) {
        WasteType.Plastic -> FilterChipColors(
            selectedContainer = Color(0xFF0E7C46),
            unselectedContainer = Color(0xFFE8F7EE),
            selectedContent = Color.White,
            unselectedContent = Color(0xFF0E7C46)
        )
        WasteType.Glass -> FilterChipColors(
            selectedContainer = Color(0xFF00897B),
            unselectedContainer = Color(0xFFE0F7F4),
            selectedContent = Color.White,
            unselectedContent = Color(0xFF00897B)
        )
        WasteType.Paper -> FilterChipColors(
            selectedContainer = Color(0xFFB7791F),
            unselectedContainer = Color(0xFFFFF3D7),
            selectedContent = Color.White,
            unselectedContent = Color(0xFF9A5E10)
        )
        WasteType.Batteries -> FilterChipColors(
            selectedContainer = Color(0xFF6D5BD0),
            unselectedContainer = Color(0xFFEDEBFF),
            selectedContent = Color.White,
            unselectedContent = Color(0xFF5B4AC4)
        )
        WasteType.Electronics -> FilterChipColors(
            selectedContainer = Color(0xFF455A64),
            unselectedContainer = Color(0xFFECEFF1),
            selectedContent = Color.White,
            unselectedContent = Color(0xFF455A64)
        )
        WasteType.Organic -> FilterChipColors(
            selectedContainer = Color(0xFF558B2F),
            unselectedContainer = Color(0xFFEAF4E1),
            selectedContent = Color.White,
            unselectedContent = Color(0xFF558B2F)
        )
        WasteType.Metal -> FilterChipColors(
            selectedContainer = Color(0xFF607D8B),
            unselectedContainer = Color(0xFFE7EEF1),
            selectedContent = Color.White,
            unselectedContent = Color(0xFF607D8B)
        )
    }
}

@Composable
private fun MapMarker(
    modifier: Modifier = Modifier,
    color: Color,
    icon: ImageVector
) {
    Box(
        modifier = modifier
            .size(38.dp)
            .background(color, CircleShape)
            .border(3.dp, Color.White, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun RecyclingPointPreviewCard(
    point: RecyclingPoint,
    onClick: () -> Unit,
    onRouteClick: () -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    userLocation: GeoPoint?,
    routeActive: Boolean,
    selectedRouteMode: RouteMode,
    onRouteModeSelected: (RouteMode) -> Unit,
    streetRoute: StreetRouteResult?,
    routeLoading: Boolean,
    routeError: String?,
    modifier: Modifier = Modifier
) {
    val strings = LocalEcoStrings.current

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 10.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(width = 46.dp, height = 4.dp)
                    .background(Color(0xFFD9DED8), RoundedCornerShape(4.dp))
                    .clickable { onExpandedChange(!expanded) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExpandedChange(!expanded) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color(0xFFE8F5E5), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Recycling,
                        contentDescription = null,
                        tint = EcoGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.size(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = point.name.replace("Green ", "GreenPoint "),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = strings.openUntil,
                        color = Color(0xFF69726B),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(EcoGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = "Open details",
                        tint = Color.White
                    )
                }
            }

            if (!expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = EcoGreen,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = strings.distanceAway(point.rating, point.distanceMeters),
                        color = Color(0xFF366B42),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                return@Column
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = EcoGreen,
                    modifier = Modifier.size(17.dp)
                )
                Text(
                    text = strings.distanceAway(point.rating, point.distanceMeters),
                    color = Color(0xFF366B42),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                InfoTile(
                    title = strings.acceptedItems,
                    value = point.acceptedWasteTypes.take(3).joinToString(", ") { strings.wasteTypeName(it) },
                    modifier = Modifier.weight(1f)
                )
                InfoTile(
                    title = strings.reward,
                    value = strings.points(point.rewardPoints),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MapCardActionButton(
                    label = "Route",
                    icon = Icons.Filled.LocationOn,
                    onClick = onRouteClick,
                    modifier = Modifier.weight(1f)
                )
                MapCardActionButton(
                    label = "Details",
                    icon = Icons.Filled.ArrowForward,
                    onClick = onClick,
                    modifier = Modifier.weight(1f)
                )
            }

            if (routeActive) {
                Spacer(modifier = Modifier.height(14.dp))
                userLocation?.let { start ->
                    RouteEstimateSection(
                        start = start,
                        destination = point,
                        selectedMode = selectedRouteMode,
                        onModeSelected = onRouteModeSelected,
                        streetRoute = streetRoute,
                        routeLoading = routeLoading,
                        routeError = routeError
                    )
                }
            }
        }
    }
}

@Composable
private fun MapCardActionButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(46.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(EcoGreen)
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = label,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun RouteEstimateSection(
    start: GeoPoint,
    destination: RecyclingPoint,
    selectedMode: RouteMode,
    onModeSelected: (RouteMode) -> Unit,
    streetRoute: StreetRouteResult?,
    routeLoading: Boolean,
    routeError: String?
) {
    val estimate = remember(start.latitude, start.longitude, destination.id, selectedMode, streetRoute) {
        buildRouteEstimate(start, destination.toGeoPoint(), selectedMode, streetRoute)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RouteMode.values().forEach { mode ->
                RouteModeChip(
                    mode = mode,
                    selected = selectedMode == mode,
                    onClick = { onModeSelected(mode) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (routeLoading || routeError != null) {
            Text(
                text = if (routeLoading) "Building street route..." else routeError.orEmpty(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            RouteMetricTile(
                title = "Distance",
                value = estimate.distanceLabel,
                modifier = Modifier.weight(1f)
            )
            RouteMetricTile(
                title = "Time",
                value = estimate.timeLabel,
                modifier = Modifier.weight(1f)
            )
            RouteMetricTile(
                title = selectedMode.extraTitle,
                value = estimate.extraLabel,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun RouteModeChip(
    mode: RouteMode,
    selected: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (selected) EcoGreen else MaterialTheme.colorScheme.surface
    val contentColor = if (selected) Color.White else MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier
            .height(34.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(containerColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = mode.icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.size(5.dp))
        Text(
            text = mode.label,
            color = contentColor,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun RouteMetricTile(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .height(58.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

@Composable
private fun InfoTile(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .height(92.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(14.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            color = EcoGreen,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun StylizedMapBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        drawRect(
            brush = Brush.verticalGradient(
                listOf(Color(0xFFC9D9C2), Color(0xFFAEC5A7), Color(0xFFB9CDB1))
            )
        )
        drawMapGrid()
        drawMapRoads()
        drawCircle(
            color = Color(0xFF7FA273).copy(alpha = 0.22f),
            radius = size.minDimension * 0.36f,
            center = Offset(size.width * 0.18f, size.height * 0.40f)
        )
    }
}

private fun DrawScope.drawMapGrid() {
    val thin = Stroke(width = 1.dp.toPx())
    val color = Color.White.copy(alpha = 0.22f)
    var x = -size.width
    while (x < size.width * 2) {
        drawLine(
            color = color,
            start = Offset(x, 0f),
            end = Offset(x + size.width * 0.45f, size.height),
            strokeWidth = thin.width
        )
        x += 28.dp.toPx()
    }
    var y = 0f
    while (y < size.height) {
        drawLine(
            color = color,
            start = Offset(0f, y),
            end = Offset(size.width, y + 42.dp.toPx()),
            strokeWidth = thin.width
        )
        y += 32.dp.toPx()
    }
}

private fun DrawScope.drawMapRoads() {
    val majorRoad = Stroke(width = 5.dp.toPx())
    val minorRoad = Stroke(width = 2.dp.toPx())
    val roadColor = Color.White.copy(alpha = 0.72f)
    val minorRoadColor = Color.White.copy(alpha = 0.40f)

    val vertical = Path().apply {
        moveTo(size.width * 0.56f, 0f)
        cubicTo(size.width * 0.52f, size.height * 0.22f, size.width * 0.68f, size.height * 0.44f, size.width * 0.48f, size.height)
    }
    drawPath(vertical, roadColor, style = majorRoad)

    val horizontal = Path().apply {
        moveTo(0f, size.height * 0.58f)
        cubicTo(size.width * 0.22f, size.height * 0.47f, size.width * 0.56f, size.height * 0.64f, size.width, size.height * 0.48f)
    }
    drawPath(horizontal, roadColor, style = majorRoad)

    repeat(8) { index ->
        val offset = index * size.height / 8f
        val path = Path().apply {
            moveTo(0f, offset)
            cubicTo(size.width * 0.28f, offset + 40.dp.toPx(), size.width * 0.54f, offset - 24.dp.toPx(), size.width, offset + 20.dp.toPx())
        }
        drawPath(path, minorRoadColor, style = minorRoad)
    }
}

private const val LOCATION_CHOICE_KEY = "location_choice"
private const val LOCATION_CHOICE_ALWAYS = "always"
private const val LOCATION_CHOICE_NEVER = "never"

private fun Context.hasLocationPermission(): Boolean {
    val fineLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
    val coarseLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
    return fineLocation == PackageManager.PERMISSION_GRANTED ||
        coarseLocation == PackageManager.PERMISSION_GRANTED
}

private fun Context.isDeviceLocationEnabled(): Boolean {
    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        locationManager.isLocationEnabled
    } else {
        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
}

private fun Context.openLocationSettings() {
    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
}

private fun createWasteMarkerIcon(
    context: Context,
    wasteType: WasteType,
    selected: Boolean
): BitmapDrawable {
    val width = if (selected) 74 else 62
    val height = if (selected) 84 else 72
    val centerX = width / 2f
    val circleRadius = if (selected) 25f else 21f
    val circleCenterY = circleRadius + 7f
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = AndroidCanvas(bitmap)
    val markerColor = wasteType.markerColor()

    val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = markerColor
        style = Paint.Style.FILL
    }
    val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = if (selected) 6f else 4f
    }
    val symbolPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = if (selected) 25f else 21f
        isFakeBoldText = true
    }

    val pointer = AndroidPath().apply {
        moveTo(centerX - 13f, circleCenterY + circleRadius - 2f)
        lineTo(centerX + 13f, circleCenterY + circleRadius - 2f)
        lineTo(centerX, height - 5f)
        close()
    }

    canvas.drawPath(pointer, fillPaint)
    canvas.drawCircle(centerX, circleCenterY, circleRadius, fillPaint)
    canvas.drawCircle(centerX, circleCenterY, circleRadius, strokePaint)
    canvas.drawText(wasteType.markerLetter(), centerX, circleCenterY + 8f, symbolPaint)

    return BitmapDrawable(context.resources, bitmap)
}

private fun createUserLocationMarkerIcon(context: Context): BitmapDrawable {
    val size = 58
    val center = size / 2f
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = AndroidCanvas(bitmap)
    val haloPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.argb(58, 33, 150, 243)
        style = Paint.Style.FILL
    }
    val outlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        style = Paint.Style.FILL
    }
    val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.rgb(33, 150, 243)
        style = Paint.Style.FILL
    }

    canvas.drawCircle(center, center, 26f, haloPaint)
    canvas.drawCircle(center, center, 13f, outlinePaint)
    canvas.drawCircle(center, center, 9f, dotPaint)

    return BitmapDrawable(context.resources, bitmap)
}

private fun WasteType.markerColor(): Int {
    return when (this) {
        WasteType.Plastic -> android.graphics.Color.rgb(14, 124, 70)
        WasteType.Glass -> android.graphics.Color.rgb(0, 137, 123)
        WasteType.Paper -> android.graphics.Color.rgb(183, 121, 31)
        WasteType.Batteries -> android.graphics.Color.rgb(109, 91, 208)
        WasteType.Electronics -> android.graphics.Color.rgb(69, 90, 100)
        WasteType.Organic -> android.graphics.Color.rgb(85, 139, 47)
        WasteType.Metal -> android.graphics.Color.rgb(96, 125, 139)
    }
}

private fun WasteType.markerLetter(): String {
    return when (this) {
        WasteType.Plastic -> "P"
        WasteType.Glass -> "G"
        WasteType.Paper -> "Pa"
        WasteType.Batteries -> "B"
        WasteType.Electronics -> "E"
        WasteType.Organic -> "O"
        WasteType.Metal -> "M"
    }
}

private fun RecyclingPoint.toGeoPoint(): GeoPoint = GeoPoint(latitude, longitude)

private fun buildRoutePreviewPoints(start: GeoPoint, destination: RecyclingPoint): List<GeoPoint> {
    val end = destination.toGeoPoint()
    val midPoint = GeoPoint(
        (start.latitude + end.latitude) / 2 + 0.006,
        (start.longitude + end.longitude) / 2 - 0.004
    )

    return listOf(start, midPoint, end)
}

private enum class RouteMode(
    val label: String,
    val speedKmh: Double,
    val roadFactor: Double,
    val extraTitle: String,
    val icon: ImageVector
) {
    Walk("Walk", 4.8, 1.18, "Steps", Icons.Filled.DirectionsWalk),
    Bike("Bike", 14.0, 1.14, "Calories", Icons.Filled.PedalBike),
    Scooter("Scooter", 18.0, 1.12, "Battery", Icons.Filled.ElectricScooter),
    Motorcycle("Moto", 32.0, 1.25, "Fuel", Icons.Filled.TwoWheeler),
    Car("Car", 28.0, 1.28, "Fuel", Icons.Filled.DirectionsCar),
    Taxi("Taxi", 30.0, 1.28, "Price", Icons.Filled.LocalTaxi),
    Bus("Bus", 20.0, 1.36, "Stops", Icons.Filled.DirectionsBus)
}

private data class RouteEstimate(
    val distanceLabel: String,
    val timeLabel: String,
    val extraLabel: String
)

private data class StreetRouteResult(
    val points: List<GeoPoint>,
    val distanceMeters: Double,
    val durationSeconds: Double
)

private fun buildRouteEstimate(
    start: GeoPoint,
    end: GeoPoint,
    mode: RouteMode,
    streetRoute: StreetRouteResult? = null
): RouteEstimate {
    val routeDistanceKm = streetRoute?.let { it.distanceMeters / 1000.0 }
        ?: (haversineDistanceKm(start, end) * mode.roadFactor).coerceAtLeast(0.05)
    val minutes = if (streetRoute != null && mode.usesTrafficDuration) {
        (streetRoute.durationSeconds / 60).roundToInt().coerceAtLeast(1)
    } else {
        ((routeDistanceKm / mode.speedKmh) * 60).roundToInt().coerceAtLeast(1)
    }

    return RouteEstimate(
        distanceLabel = formatDistance(routeDistanceKm),
        timeLabel = formatMinutes(minutes),
        extraLabel = mode.extraLabel(routeDistanceKm, minutes)
    )
}

private fun haversineDistanceKm(start: GeoPoint, end: GeoPoint): Double {
    val earthRadiusKm = 6371.0
    val startLat = Math.toRadians(start.latitude)
    val endLat = Math.toRadians(end.latitude)
    val deltaLat = Math.toRadians(end.latitude - start.latitude)
    val deltaLon = Math.toRadians(end.longitude - start.longitude)
    val a = sin(deltaLat / 2) * sin(deltaLat / 2) +
        cos(startLat) * cos(endLat) * sin(deltaLon / 2) * sin(deltaLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return earthRadiusKm * c
}

private fun formatDistance(distanceKm: Double): String {
    return if (distanceKm < 1.0) {
        "${(distanceKm * 1000).roundToInt()} m"
    } else {
        String.format("%.1f km", distanceKm)
    }
}

private fun formatMinutes(minutes: Int): String {
    return if (minutes < 60) {
        "$minutes min"
    } else {
        val hours = minutes / 60
        val restMinutes = minutes % 60
        if (restMinutes == 0) "${hours}h" else "${hours}h ${restMinutes}m"
    }
}

private fun RouteMode.extraLabel(distanceKm: Double, minutes: Int): String {
    return when (this) {
        RouteMode.Walk -> "${(distanceKm * 1320).roundToInt()} steps"
        RouteMode.Bike -> "${(distanceKm * 28).roundToInt()} kcal"
        RouteMode.Scooter -> "${(distanceKm * 7).roundToInt().coerceAtLeast(1)}%"
        RouteMode.Motorcycle -> String.format("%.1f L", distanceKm * 0.035)
        RouteMode.Car -> String.format("%.1f L", distanceKm * 0.09)
        RouteMode.Taxi -> "${estimateTaxiPrice(distanceKm, minutes)} KZT"
        RouteMode.Bus -> "${(minutes / 7).coerceAtLeast(1)} stops"
    }
}

private fun estimateTaxiPrice(distanceKm: Double, minutes: Int): Int {
    val rawPrice = 450 + distanceKm * 120 + minutes * 18
    return ((rawPrice / 50).roundToInt() * 50).coerceAtLeast(600)
}

private suspend fun fetchStreetRoute(
    start: GeoPoint,
    end: GeoPoint,
    mode: RouteMode
): StreetRouteResult = withContext(Dispatchers.IO) {
    val url = URL(
        "https://router.project-osrm.org/route/v1/${mode.osrmProfile}/" +
            "${start.longitude},${start.latitude};${end.longitude},${end.latitude}" +
            "?overview=full&geometries=geojson&steps=false"
    )
    val json = url.openConnection().run {
        connectTimeout = 8_000
        readTimeout = 8_000
        getInputStream().bufferedReader().use { it.readText() }
    }
    val root = JSONObject(json)
    val routes = root.getJSONArray("routes")
    if (routes.length() == 0) error("No route found")

    val route = routes.getJSONObject(0)
    val coordinates = route
        .getJSONObject("geometry")
        .getJSONArray("coordinates")
    val points = buildList {
        for (index in 0 until coordinates.length()) {
            val coordinate = coordinates.getJSONArray(index)
            add(GeoPoint(coordinate.getDouble(1), coordinate.getDouble(0)))
        }
    }

    StreetRouteResult(
        points = points,
        distanceMeters = route.getDouble("distance"),
        durationSeconds = route.getDouble("duration")
    )
}

private val RouteMode.osrmProfile: String
    get() = "driving"

private val RouteMode.usesTrafficDuration: Boolean
    get() = this == RouteMode.Motorcycle || this == RouteMode.Car || this == RouteMode.Taxi

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun MapScreenPreview() {
    EcoDalaTheme {
        MapScreen(
            uiState = MapUiState(),
            onSearchQueryChange = {},
            onWasteTypeSelected = {},
            onPointSelected = {},
            onRouteClick = {},
            onPointClick = {}
        )
    }
}
