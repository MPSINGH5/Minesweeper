package com.example.minesweeper

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.core.view.isVisible

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val easy:RadioButton=findViewById(R.id.Easy)
        val medium:RadioButton=findViewById(R.id.Medium)
        val hard:RadioButton=findViewById(R.id.Hard)
        val board: Button =findViewById(R.id.button3)
        val start:Button=findViewById(R.id.START)
        val best:TextView=findViewById(R.id.BestTime)
        val last:TextView=findViewById(R.id.CurrentTime)
        val row:TextView=findViewById(R.id.Rows)
        val column:TextView=findViewById(R.id.Columns)
        val mines:TextView=findViewById(R.id.Mines)
        val sharedPref = getSharedPreferences("Minesweeper",Context.MODE_PRIVATE)
        var highScore: Int = sharedPref.getInt("High Score", Int.MAX_VALUE)
        var lastGame=sharedPref.getInt("Last Time", Int.MAX_VALUE)
        if(lastGame== Int.MAX_VALUE)
        {
            last.text="Last game time: No record of previous games"
        }
        else
        {
            last.text= "Last game time: "+ ((lastGame/1000)/60)+" m "+((lastGame/1000)%60)+" s" //Setting last game time
        }
        if(highScore== Int.MAX_VALUE)
        {
            best.text="Best Time: No record of previous games"
        }
        else
        {
            best.text= "Best Time: "+((highScore/1000)/60)+" m "+((highScore/1000)%60)+" s"// Setting best time
        }

        //Setting OnCliCkListeners for easy,medium and hard radio buttons
        easy.setOnClickListener{
            medium.isChecked=false
            hard.isChecked=false
            easy.isChecked=true
        }
        medium.setOnClickListener{
            medium.isChecked=true
            hard.isChecked=false
            easy.isChecked=false
        }
        hard.setOnClickListener{
            medium.isChecked=false
            hard.isChecked=true
            easy.isChecked=false
        }
        //Making a custom board
        board.setOnClickListener{
            //First disaabling/enabling easy,medium and hard radio buttons
            medium.isEnabled=!medium.isEnabled
            hard.isEnabled=  !hard.isEnabled
            easy.isEnabled=! easy.isEnabled
            row.isVisible=!row.isVisible
            column.isVisible=!column.isVisible
            mines.isVisible=! mines.isVisible

        }
        start.setOnClickListener{
            if(medium.isEnabled||   hard.isEnabled || easy.isEnabled)
            {
                if( easy.isChecked)
                {   //Calling intent for easy board
                    val intent = Intent(this, Board::class.java).apply{
                        putExtra("ROWS",9)
                        putExtra("COLUMNS",9)
                        putExtra("MINES",10)
                        putExtra("TIME",best.text.toString().trim())
                    }
                    startActivity(intent)
                }
                else if( medium.isChecked)
                {   //Calling intent for mediunm board
                    val intent = Intent(this, Board::class.java).apply{
                        putExtra("ROWS",16)
                        putExtra("COLUMNS",16)
                        putExtra("MINES",40)
                        putExtra("TIME",best.text.toString().trim())
                    }
                    startActivity(intent)
                }
                else if(  hard.isChecked)
                {   //Calling intent for hard difficulty
                    val intent = Intent(this, Board::class.java).apply{
                        putExtra("ROWS",30)
                        putExtra("COLUMNS",16)
                        putExtra("MINES",99)
                        putExtra("TIME",best.text.toString().trim())
                    }
                    startActivity(intent)

                     highScore= sharedPref.getInt("High Score", Int.MAX_VALUE)
                     lastGame=sharedPref.getInt("Last Time", Int.MAX_VALUE)
                    if(lastGame== Int.MAX_VALUE)
                    {
                        last.text="Last game time: No record of previous games"
                    }
                    else
                    {
                        last.text= "Last game time: "+ ((lastGame/1000)/60)+" m "+((lastGame/1000)%60)+" s" //Setting last game time
                    }
                    if(highScore== Int.MAX_VALUE)
                    {
                        best.text="Best Time: No record of previous games"
                    }
                    else
                    {
                        best.text= "Best Time: "+((highScore/1000)/60)+" m "+((highScore/1000)%60)+" s"// Setting best time
                    }


                }

            }
            else
            {
                if(row.text.toString().trim()=="" || column.text.toString().trim()=="" || mines.text.toString().trim()=="")
                    Toast.makeText(this,"Enter the required values first", Toast.LENGTH_SHORT).show() //Checking for empty parameters in custom board
                else if(mines.text.toString().trim().toInt()*4>(row.text.toString().trim().toInt()*column.text.toString().trim().toInt()))
                {   //Putting constraint that number of mines should not be greater than 1/4 of total number of board boxes
                    Toast.makeText(this,"Number of mines should not be greater than 1/4 of total number of board boxes", Toast.LENGTH_SHORT).show()
                }
                else if(column.text.toString().trim().toInt()>row.text.toString().trim().toInt())
                {   //Putting constraint that number of columns should be less than or equal to the number of rows
                    Toast.makeText(this,"Number of columns should be less than or equal to the number of rows", Toast.LENGTH_SHORT).show()
                }
                else if(column.text.toString().trim().toInt()==0)
                {
                    Toast.makeText(this,"Number of columns should not be 0", Toast.LENGTH_SHORT).show()
                }
                else if(row.text.toString().trim().toInt()>30)
                {
                    Toast.makeText(this,"Number of rows should not be greater than 30", Toast.LENGTH_SHORT).show()///Setting max limit of 30 on rows
                }
                else if(column.text.toString().trim().toInt()>16)
                {
                    Toast.makeText(this,"Number of columns should not be greater than 16", Toast.LENGTH_SHORT).show()///Setting max limit of 16 on columns
                }

                else
                {
                    val intent = Intent(this, Board::class.java).apply{
                        putExtra("ROWS",row.text.toString().trim().toInt())
                        putExtra("COLUMNS",column.text.toString().trim().toInt())
                        putExtra("MINES",mines.text.toString().trim().toInt())
                        putExtra("TIME",best.text.toString().trim())
                    }
                    startActivity(intent)

                     highScore= sharedPref.getInt("High Score", Int.MAX_VALUE)
                     lastGame=sharedPref.getInt("Last Time", Int.MAX_VALUE)
                    if(lastGame== Int.MAX_VALUE)
                    {
                        last.text="Last game time: No record of previous games"
                    }
                    else
                    {
                        last.text= "Last game time: "+ ((lastGame/1000)/60)+" m "+((lastGame/1000)%60)+" s" //Setting last game time
                    }
                    if(highScore== Int.MAX_VALUE)
                    {
                        best.text="Best Time: No record of previous games"
                    }
                    else
                    {
                        best.text= "Best Time: "+((highScore/1000)/60)+" m "+((highScore/1000)%60)+" s"// Setting best time
                    }




                }
            }
        }





    }
}