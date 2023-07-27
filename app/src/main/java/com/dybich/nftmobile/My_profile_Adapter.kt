package com.dybich.nftmobile

import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dybich.nftmobile.databinding.CardCellBinding

import com.facebook.shimmer.ShimmerFrameLayout
import com.squareup.picasso.Picasso

class My_profile_Adapter(private val nftsLIST: List<NFT>) : RecyclerView.Adapter<My_profile_ViewHolder>()

{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): My_profile_ViewHolder {
       val layoutInflater = LayoutInflater.from(parent.context)
        val binding = CardCellBinding.inflate(layoutInflater, parent,false)
        return My_profile_ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return nftsLIST.size
    }

    override fun onBindViewHolder(holder: My_profile_ViewHolder, position: Int) {
        holder.bindNFT(nftsLIST[position])
    }
}

class My_profile_ViewHolder(
    private val cardCellBinding: CardCellBinding
): RecyclerView.ViewHolder(cardCellBinding.root){
    fun bindNFT(nft: NFT){


        Handler().postDelayed({
            val myNFTPicShimmer = cardCellBinding.myNFTPicShimmer
            myNFTPicShimmer.hideShimmer()
            myNFTPicShimmer.background = null

            val mynftNameShimmer = cardCellBinding.myNftNameShimmer
            mynftNameShimmer.hideShimmer()
            mynftNameShimmer.background = null
            val mynftPriceShimmer = cardCellBinding.myNftPriceShimmer
            mynftPriceShimmer.hideShimmer()

            //Picasso.get().load("${nft.img}").into(cardCellBinding.myNFTPicIV)
            Picasso.get().load(nft.img).into(cardCellBinding.myNFTPicIV)
            cardCellBinding.myNftNameTV.text = nft.name
            cardCellBinding.myNftPriceTV.text = nft.price
        },2000)

    }
}