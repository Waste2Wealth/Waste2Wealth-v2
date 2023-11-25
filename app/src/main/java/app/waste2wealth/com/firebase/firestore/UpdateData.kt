package app.waste2wealth.com.firebase.firestore

import android.content.Context
import android.widget.Toast
import app.waste2wealth.com.communities.ui.DummyCards
import app.waste2wealth.com.tags.Groups
import app.waste2wealth.com.tags.Tag
import app.waste2wealth.com.tags.TagWithoutTips
import coil.request.Tags
import com.google.firebase.firestore.FirebaseFirestore

fun updateInfoToFirebase(
    context: Context,
    name: String?,
    email: String?,
    phoneNumber: String?,
    gender: String?,
    organization: String?,
    address: String?,
    pointsEarned: Int,
    pointsRedeemed: Int,
    noOfTimesReported: Int = 0,
    noOfTimesCollected: Int = 0,
    noOfTimesActivity: Int = 0,
    communities: List<String> = emptyList()
) {
    val profile = ProfileInfo(
        name,
        email,
        phoneNumber,
        gender,
        organization,
        address,
        pointsEarned,
        pointsRedeemed,
        noOfTimesReported,
        noOfTimesCollected,
        noOfTimesActivity,
        communities
    )

    val db = FirebaseFirestore.getInstance()
    email?.let {
        db.collection("ProfileInfo").document(it).set(profile)
            .addOnSuccessListener {

                Toast.makeText(context, "Profile Updated successfully..", Toast.LENGTH_SHORT).show()

            }.addOnFailureListener { exception ->
                Toast.makeText(
                    context,
                    "Fail to update Profile : " + exception.message,
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
    }

}

fun updateWasteToFirebase(
    context: Context,
    latitude: Double,
    longitude: Double,
    imagePath: String,
    timeStamp: Long,
    userEmail: String,
    address: String,
    tags: List<TagWithoutTips> = emptyList(),
) {
    val wasteItem = WasteItem(
        latitude, longitude, imagePath, timeStamp, userEmail, address, tags
    )

    val db = FirebaseFirestore.getInstance()
    timeStamp.let {
        db.collection("TempAllWastes").document(it.toString()).set(wasteItem)
            .addOnSuccessListener {

                Toast.makeText(context, "Waste Reported Successfully", Toast.LENGTH_SHORT).show()

            }.addOnFailureListener { exception ->
                Toast.makeText(
                    context,
                    "Fail to Report Waste " + exception.message,
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
    }


}

fun updateCollectedWasteToFirebase(
    context: Context,
    latitude: Double,
    longitude: Double,
    imagePath: String,
    timeStamp: Long,
    userEmail: String,
    address: String,
    isWasteCollected: Boolean,
    allWasteCollected: Boolean,
    feedBack: String,
) {
    val wasteItem = CollectedWasteItem(
        latitude,
        longitude,
        imagePath,
        timeStamp,
        userEmail,
        address,
        isWasteCollected,
        allWasteCollected,
        feedBack
    )

    val db = FirebaseFirestore.getInstance()
    timeStamp.let {
        db.collection("CollectedWastes").document(it.toString()).set(wasteItem)
            .addOnSuccessListener {

                Toast.makeText(context, "Waste Reported Successfully", Toast.LENGTH_SHORT).show()

            }.addOnFailureListener { exception ->
                Toast.makeText(
                    context,
                    "Fail to Report Waste " + exception.message,
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
    }


}

fun updateCommunitiesToFirebase(
    communities: List<DummyCards>
) {
    val db = FirebaseFirestore.getInstance()
    communities.forEach {
        db.collection("Communities").document(it.name).set(it)
            .addOnSuccessListener {
                println("Communities Updated successfully..")

            }.addOnFailureListener { exception ->
                println("Fail to update Communities : " + exception.message)
            }
    }
}

fun updateTagsToFirebase(
    tags: List<Groups>
) {
    val db = FirebaseFirestore.getInstance()
    tags.forEach {
        db.collection("TagGroups").document(it.name).set(it)
            .addOnSuccessListener {
                println("Communities Updated successfully..")

            }.addOnFailureListener { exception ->
                println("Fail to update Communities : " + exception.message)
            }
    }
}

