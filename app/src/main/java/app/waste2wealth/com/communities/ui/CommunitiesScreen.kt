package app.waste2wealth.com.communities.ui

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.waste2wealth.com.R
import app.waste2wealth.com.firebase.firestore.ProfileInfo
import app.waste2wealth.com.ui.theme.appBackground
import app.waste2wealth.com.ui.theme.monteBold
import app.waste2wealth.com.ui.theme.monteNormal
import app.waste2wealth.com.ui.theme.textColor
import com.jet.firestore.JetFirestore
import com.jet.firestore.getListOfObjects
import app.waste2wealth.com.communities.CommunitiesViewModel
import app.waste2wealth.com.profile.ProfileImage
import app.waste2wealth.com.utils.AutoResizedText
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CommunitiesSection(
    name:String,
    paddingValues: PaddingValues,
    email: String
) {
    var profileList by remember {
        mutableStateOf<List<ProfileInfo>?>(null)
    }
    var userAddress by remember {
        mutableStateOf("")
    }
    var phoneNumber by remember {
        mutableStateOf("")
    }
    var gender by remember {
        mutableStateOf("")
    }
    var organization by remember {
        mutableStateOf("")
    }
    var pointsEarned by remember {
        mutableStateOf(0)
    }
    var pointsRedeemed by remember {
        mutableStateOf(0)
    }
    var noOfTimesReported by remember {
        mutableStateOf(0)
    }
    var noOfTimesCollected by remember {
        mutableStateOf(0)
    }
    var noOfTimesActivity by remember {
        mutableStateOf(0)
    }
    var communities by remember { mutableStateOf<List<DummyCards>?>(null) }
    var allCommunities by remember { mutableStateOf<List<DummyCards>?>(null) }
    var myCommunities = remember {
        mutableStateOf(mutableListOf(""))
    }
    var selectedType by remember { mutableStateOf(TypeOfCommunities.ALL_COMMUNITIES.names) }

    JetFirestore(path = {
        collection("Communities")
    }, onRealtimeCollectionFetch = { values, _ ->
        communities = values?.getListOfObjects()
        allCommunities = values?.getListOfObjects()

    }) {
        JetFirestore(path = {
            collection("ProfileInfo")
        }, onRealtimeCollectionFetch = { value, _ ->
            profileList = value?.getListOfObjects()
        }) {
            if (profileList != null) {
                for (i in profileList!!) {
                    if (i.email == email) {
                        userAddress = i.address ?: ""
                        gender = i.gender ?: ""
                        phoneNumber = i.phoneNumber ?: ""
                        organization = i.organization ?: ""
                        pointsEarned = i.pointsEarned
                        pointsRedeemed = i.pointsRedeemed
                        noOfTimesReported = i.noOfTimesReported
                        noOfTimesCollected = i.noOfTimesCollected
                        noOfTimesActivity = i.noOfTimesActivity
                        myCommunities.value = i.communities.toMutableList()
                    }
                }
            }
            LaunchedEffect(key1 = selectedType){
                when(selectedType){
                    TypeOfCommunities.ALL_COMMUNITIES.names -> {
                        allCommunities = communities
                    }
                    TypeOfCommunities.Memberships.names -> {
                        allCommunities = allCommunities?.filter { it.name in myCommunities.value }
                    }

                }
            }

            var progress2 = remember { mutableStateOf(0f) }
            val visible =
                animateFloatAsState(if (progress2.value > 0.35f) 1f else 0f, label = "").value
            val inVisible =
                animateFloatAsState(if (progress2.value > 0.35f) 0f else 1f, label = "").value
            val isLoading = remember { mutableStateOf(false) }
            var expanded by remember { mutableStateOf(false) }

            LaunchedEffect(key1 = allCommunities) {
                if(allCommunities?.isEmpty() == true || allCommunities == null){
                    isLoading.value = true
                }
            }
            LaunchedEffect(key1 = isLoading.value){
                if(isLoading.value){
                    delay(2000)
                    isLoading.value = false
                }
            }
            if (isLoading.value) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    val currenanim by rememberLottieComposition(
                        spec = LottieCompositionSpec.Asset("loading.json")
                    )
                    LottieAnimation(
                        composition = currenanim,
                        iterations = 1,
                        contentScale = ContentScale.Crop,
                        speed = 1f,
                        modifier = Modifier
                            .size(70.dp)
                    )
                }
            } else {
                allCommunities?.let { community ->
                    val viewModel: CommunitiesViewModel = remember { CommunitiesViewModel() }
                    Column(
                        modifier = Modifier
                            .background(appBackground)
                    ) {
                        AnimatedVisibility(
                            visible = viewModel.expandedState.value < 0.5f,
                            enter = expandVertically(tween(400)) + fadeIn(tween(400)),
                            exit = shrinkVertically(tween(400)) + fadeOut(tween(400)),
                            modifier = Modifier.background(Color.Transparent)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        top = 35.dp,
                                        bottom = 35.dp,
                                        start = 20.dp,
                                        end = 20.dp
                                    ),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable {
                                        expanded = true
                                    },
                                ) {
                                    ExposedDropdownMenuBox(
                                        modifier = Modifier,
                                        expanded = expanded,
                                        onExpandedChange = { expanded = it }
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.Bottom,
                                            modifier = Modifier,
                                        ) {
                                            AutoResizedText(
                                                text = selectedType,
                                                color = textColor,
                                                fontSize = 30.sp,
                                                fontFamily = monteBold,
                                            )
                                            Icon(
                                                imageVector = Icons.Default.ArrowDropDown,
                                                contentDescription = null,
                                                tint = textColor,
                                                modifier = Modifier
                                                    .size(25.dp)
                                                    .padding(bottom = 7.dp)
                                            )
                                        }

                                        DropdownMenu(
                                            expanded = expanded,
                                            onDismissRequest = { expanded = false }
                                        ) {
                                            typeOfCommunities.forEach { type ->
                                                DropdownMenuItem(
                                                    onClick = {
                                                        selectedType = type
                                                        expanded = false
                                                    }
                                                ) {
                                                    AutoResizedText(text = type)
                                                }
                                            }
                                        }
                                    }
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 15.dp, end = 0.dp, start = 20.dp),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier.padding(end = 25.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.coins),
                                            contentDescription = "coins",
                                            modifier = Modifier
                                                .size(30.dp)
                                                .padding(end = 5.dp),
                                            tint = Color.Unspecified
                                        )
                                        AutoResizedText(
                                            text = pointsEarned.toString(),
                                            color = textColor,
                                            fontSize = 15.sp,
                                            softWrap = false,
                                            fontFamily = monteNormal,
                                        )
                                    }

                                }
                            }
                        }
                        AnimatedVisibility(
                            visible = viewModel.expandedState.value > 0.5f,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut(),
                            modifier = Modifier.background(Color.Transparent)
                        ) {
                            if (progress2.value > 0.5f) {
                                AnimatedVisibility(
                                    visible = visible > 0f,
                                    enter = fadeIn() + slideInHorizontally(),
                                    exit = fadeOut() + slideOutHorizontally(),
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                top = 30.dp,
                                                end = 25.dp
                                            )
                                            .graphicsLayer {
                                                alpha = visible
                                            },
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Card(
                                            backgroundColor =
                                            Color(0xFFE91E63).fromHex(
                                                community[viewModel.currentPage.value].cardColor
                                            ),
                                            border = BorderStroke(
                                                width = 1.dp, color = Color(0xFFE91E63).fromHex(
                                                    community[viewModel.currentPage.value].cardColor
                                                )
                                            ),
                                            shape = RoundedCornerShape(30.dp),
                                            modifier = Modifier
                                                .width(160.dp)
                                                .height(100.dp)
                                        ) {
                                            ProfileImage(
                                                imageUrl = community[viewModel.currentPage.value].image,
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .clip(RoundedCornerShape(30.dp)),
                                            )
                                        }
                                        AutoResizedText(
                                            text = community[viewModel.currentPage.value].name,
                                            fontSize = 21.sp,
                                            fontWeight = FontWeight.Bold,
                                            softWrap = true,
                                            modifier = Modifier.padding(start = 0.dp),
                                            color = textColor,

                                            )

                                    }
                                }


                            } else {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()

                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                top = 30.dp,
                                                start = 10.dp,
                                                end = 25.dp
                                            ),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        AutoResizedText(
                                            text = community[viewModel.currentPage.value].name,
                                            fontSize = 25.sp,
                                            fontWeight = FontWeight.Bold,
                                            softWrap = true,
                                            modifier = Modifier
                                                .graphicsLayer {
                                                    alpha = inVisible
                                                },
                                            color = textColor,

                                            )

                                    }
                                    Spacer(modifier = Modifier.height(20.dp))
                                }
                            }

                        }
                        Pager2(
                            viewModel,
                            progress2,
                            paddingValues,
                            allCommunities,
                            name,
                            email,
                            userAddress,
                            gender,
                            phoneNumber,
                            organization,
                            pointsEarned,
                            pointsRedeemed,
                            noOfTimesReported,
                            noOfTimesCollected,
                            noOfTimesActivity,
                            myCommunities

                        )
                    }
                    Log.i("ExpandedState", "HomeScreen: ${viewModel.expandedState.value}")

                }
            }
        }
    }
}







