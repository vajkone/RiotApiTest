package com.gva.daydreamerz.riotapitest

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {



    var summname:String?=null
    var context:Context?=null
    val rgapikey="RGAPI-43d03d0b-de5c-4315-adee-31645a98ce71"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        context=this







        fetchDataButton.setOnClickListener {
            fetchJson()
        }

    }


    fun fetchJson(){

        summname=summonerName.text.toString()

        var url = "https://eun1.api.riotgames.com/lol/summoner/v4/summoners/by-name/"+summname+"?api_key="+rgapikey

        var request=Request.Builder().url(url).build()


        val client = OkHttpClient()
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                Toast.makeText(context,"Failed to execute",Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call, response: Response) {

                val body=response.body!!.string()
                println(body)

                val gson = GsonBuilder().create()

                val summoner:Summoner=gson.fromJson(body,Summoner::class.java)


                val id=summoner.id

                runOnUiThread {

                    levelText!!.text=summoner.summonerLevel.toString()
                }

                url = "https://eun1.api.riotgames.com/lol/league/v4/entries/by-summoner/"+id+"?api_key="+rgapikey



                request=Request.Builder().url(url).build()

                client.newCall(request).enqueue(object: Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        runOnUiThread {
                        Toast.makeText(context,"Failed to execute",Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {

                        val bodee=response.body!!.string()
                        println(bodee)

                        val gsone = GsonBuilder().create()

                        val ranked:List<RankedStats> = gsone.fromJson(bodee,Array<RankedStats>::class.java).toList()



                        if (ranked.size==1){
                            var soloduo=ranked.get(0)
                            if (soloduo.queueType=="RANKED_SOLO_5x5")
                            runOnUiThread {
                                soloDuoText!!.text=
                                    "${soloduo!!.tier} ${soloduo.rank} ${soloduo.leaguePoints} lp"
                                rankedFlexText.text="Unranked"
                                soloDuoText3.text= "W: ${soloduo.wins} L: ${soloduo.losses}"
                            }
                            else if(soloduo.queueType=="RANKED_FLEX_SR"){
                                runOnUiThread {
                                    rankedFlexText.text =
                                        "${soloduo!!.tier} ${soloduo.rank} ${soloduo.leaguePoints} lp"
                                    rankedFlexText3.text="W: ${soloduo.wins} L: ${soloduo.losses}"
                                    soloDuoText.text="Unranked"
                                }
                            }

                        }else if(ranked.size==2){
                            for (x in 0 until ranked.size){

                                var soloduo= ranked[x]

                                if (soloduo.queueType=="RANKED_SOLO_5x5")
                                    runOnUiThread {
                                        soloDuoText!!.text=
                                            "${soloduo!!.tier} ${soloduo.rank} ${soloduo.leaguePoints} lp"
                                        soloDuoText3.text= "W: ${soloduo.wins} L: ${soloduo.losses}"
                                    } else if(soloduo.queueType=="RANKED_FLEX_SR"){
                                    runOnUiThread {
                                        rankedFlexText.text =
                                            "${soloduo!!.tier} ${soloduo.rank} ${soloduo.leaguePoints} lp"
                                        rankedFlexText3.text="W: ${soloduo.wins} L: ${soloduo.losses}"
                                    }
                                }

                            }
                        }else{
                            runOnUiThread {
                                rankedFlexText.text ="Unranked"
                                soloDuoText.text="Unranked"
                                }
                        }



                        println(bodee)






                    }

                })


            }

        })


    }

}

class Summoner(val id:String,val accountId:String,
               val puuid:String,val name:String,
               val profileIconId:Int,val revisionDate:Long,
               val summonerLevel:Int)

class Ranked(val stats:List<RankedStats>)

class RankedStats(val queueType:String, val tier:String,val rank:String,val leaguePoints:Int,val wins:Int,val losses:Int)


