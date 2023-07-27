package com.dybich.nftmobile

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.lifecycle.SAVED_STATE_REGISTRY_OWNER_KEY
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dybich.nftmobile.databinding.FragmentMyProfileBinding
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import com.facebook.shimmer.ShimmerFrameLayout

import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


class My_Profile_Fragment : Fragment() {

    private lateinit var shimmerIV:ShimmerFrameLayout
    private lateinit var shimmerLoginTV:ShimmerFrameLayout
    private lateinit var shimmerEmailTV:ShimmerFrameLayout


    private lateinit var tvemail: TextView
    private lateinit var tvlogin: TextView
    private lateinit var pictureIV:ImageView

    private lateinit var email:String
    private lateinit var login:String
    private lateinit var profilePic:String
    private lateinit var database: DatabaseReference

    private lateinit var auth: FirebaseAuth

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: My_profile_Adapter


    private lateinit var nftsLIST : ArrayList<NFT>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = Firebase.database.reference
        nftsLIST= arrayListOf<NFT>()
        adapter = My_profile_Adapter(nftsLIST)

        auth = FirebaseAuth.getInstance()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_my_profile, container, false)


        shimmerIV = view.findViewById(R.id.shimmerIV)
        shimmerEmailTV = view.findViewById(R.id.shimmerEmailTV)
        shimmerLoginTV = view.findViewById(R.id.shimmerLoginTV)

        recyclerView = view.findViewById(R.id.myNFT_RV)


        tvemail = view.findViewById(R.id.loggedinEmail)
        tvlogin = view.findViewById(R.id.loggedinUsername)
        pictureIV = view.findViewById(R.id.profilePic)

        if(savedInstanceState != null){
            shimmerLoginTV.hideShimmer()
            shimmerLoginTV.background = null
            shimmerEmailTV.hideShimmer()
            shimmerEmailTV.background = null
            shimmerIV.hideShimmer()

            val db = Firebase.database
            val user = auth.currentUser
            val uid = user?.uid
            val userData=   db.getReference("/users/$uid/")
            userData.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    login = snapshot.child("username").getValue() as String

                    email = snapshot.child("email").getValue() as String
                    tvemail.text = email
                    tvlogin.text = login
                    if(snapshot.child("profile_Pic").exists()){
                        profilePic = snapshot.child("profile_Pic").getValue() as String
                        Picasso.get().load("$profilePic").into(pictureIV)
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

        }

        else{
            getLIST(view)
            getAccData(tvemail,tvlogin,pictureIV)

        }


        logout(view)
        return view
    }

    private fun getLIST(view: View){

        val db = Firebase.database
        val user = auth.currentUser
        val uid = user?.uid
        val userData=   db.getReference("/users/$uid/nfts")
        userData.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
               if(snapshot.exists()){
                   val gridLayoutManager = GridLayoutManager(context,2)

                   recyclerView.layoutManager = gridLayoutManager

                   for(userSnapshot in snapshot.children){
                       val objnft = userSnapshot.getValue(NFT::class.java)
                       if (objnft != null) {
                           nftsLIST.add(objnft)
                       }
                   }

                   recyclerView.adapter = adapter
               }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

    }




    private fun logout(view: View){
        val logout = view.findViewById<Button>(R.id.btnlogout)
        logout.setOnClickListener(){
            requireActivity().run {

                Firebase.auth.signOut()
                startActivity(Intent(this, MainActivity::class.java))
                overridePendingTransition(
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
                finishAffinity()
                finish()

            }
        }
    }


    private fun getAccData(tvemail : TextView,tvlogin: TextView,pictureIV:ImageView){
        val fadeIN= AnimationUtils.loadAnimation(context, androidx.transition.R.anim.abc_fade_in)
        val fadeOut = AnimationUtils.loadAnimation(context, androidx.transition.R.anim.abc_fade_out)

        val db = Firebase.database
        val user = auth.currentUser
        val uid = user?.uid
        val userData=   db.getReference("/users/$uid/")
        userData.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
               login = snapshot.child("username").getValue() as String

                email = snapshot.child("email").getValue() as String


                Handler().postDelayed({
                    shimmerLoginTV.hideShimmer()
                    shimmerLoginTV.background = null
                    shimmerLoginTV.startAnimation(fadeOut)
                    tvlogin.text = login
                    tvlogin.startAnimation(fadeIN)

                },1000)
                Handler().postDelayed({
                    shimmerEmailTV.hideShimmer()
                    shimmerEmailTV.background = null
                    shimmerEmailTV.startAnimation(fadeOut)
                    tvemail.text = email
                    tvemail.startAnimation(fadeIN)

                },1000)

                if(snapshot.child("profile_Pic").exists()){

                    profilePic = snapshot.child("profile_Pic").getValue() as String


                    Handler().postDelayed({
                        shimmerIV.hideShimmer()
                        shimmerIV.startAnimation(fadeOut)
                        Picasso.get().load("$profilePic").into(pictureIV)
                        pictureIV.startAnimation(fadeIN)

                    },2000)

                }
                else{
                    shimmerIV.hideShimmer()
                    shimmerIV.startAnimation(fadeOut)
                }


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

}