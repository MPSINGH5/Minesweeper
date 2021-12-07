package com.example.minesweeper

import android.Manifest
import android.Manifest.permission.SEND_SMS
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.minesweeper.R.drawable.*

class Board : AppCompatActivity() {
    lateinit var boards: LinearLayout //board layout
    lateinit var board:Array<Array<MineCell>>  //creating array of mine cells
    var status = Status.ONGOINING        //setting status
    var row:Int=0
    var column:Int=0
    var mines:Int=0
    var f=0 //f keeps record whether all mines are flagged or not
    var count:Int = 0 // Count checks if all the cells without mine is revealed or not
    var c=1
    lateinit var  minesv:TextView
    lateinit var chronometer: Chronometer
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_board)
         row = intent.getIntExtra("ROWS",0)
        column= intent.getIntExtra("COLUMNS",0)
         mines = intent.getIntExtra("MINES",0)
        boards = findViewById(R.id.Board)
        minesv=findViewById(R.id.MINESVALUE)
        minesv.text=mines.toString()
        chronometer=findViewById(R.id.timer)
        f=mines



        board= Array(row) { Array(column) { MineCell() }}
        count= row*column
        //Initialising every global variable
        setup()//Setup function sets up board


        val restart: Button = findViewById(R.id.Restart)
        //Restart button will finish acrivity
        restart.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }



    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun setup() {
        //Settting up board accordin to rows and columns
        val params1 = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0
        )
        val params2 = LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.MATCH_PARENT
        )
        for (i in 0..row-1) {
            val linearLayout = LinearLayout(this)
            linearLayout.orientation = LinearLayout.HORIZONTAL
            linearLayout.layoutParams = params1
            params1.setMargins(0,0,0,0)
            params1.weight = 1.0F
            for (j in 0..column-1) {
                val button = ImageButton(this)
                button.id =i*10+j   ////////////////////////////////////Taking buttons id as row*10+column
                val gd = GradientDrawable()
                gd.cornerRadius = 5f
                gd.setStroke(3, Color.BLACK)

                button.background=gd
                button.layoutParams = params2
                params2.weight = 1.0F
                button.setOnClickListener {

                    if(c==1)//////////////////Mines get set up on first click
                    {   chronometer.start()
                        setupMine(button)
                        c++

                    }
                    move(button)  //////////////////////////////move function when user wants to open cell

                }
                button.setOnLongClickListener{

                    if(c==1)//////////////////////Mines get set up on first click
                    {   chronometer.start()
                        setupMine(button)
                        c++

                    }
                    button.isClickable=false
                    flag(button)////////////////move function when user wants to flag cell

                    Handler().postDelayed(Runnable { button.isClickable=true}, 5000)
                }
                linearLayout.addView(button)

            }
            boards.addView(linearLayout)
        }
    }



    data class MineCell(var value:Int = 0 , var isRevealed: Boolean = false, var isMarked: Boolean = false)//Data class mine cell
    enum class Status{////////Enum class for storing status of games
        WON,
        ONGOINING,
        LOST
    }
    companion object{//////////////Companion object for storing useful information
    const val MINE = -1
        val movement = intArrayOf(-1, 0, 1) //Used for updating neighbour when mines are set up
        private val xDir = intArrayOf(-1, -1, 0, 1, 1, 1, 0, -1)
        private val yDir = intArrayOf(0, 1, 1, 1, 0, -1, -1, -1)
    }
    private fun setupMine(view: View) {/////////////Function for setting up mines
         val b=view as ImageButton
        val x=b.id/10
        val y=b.id%10
        for(i in 1..mines)
        {
            var r=(0..row-1).random()///////////////////Generating random row for mines
            var c=(0..column-1).random()//////////////////Generating random columns for mines
            if(x==r && y==c)
            {
                while(x==r && y==c)
                {r=(0..row-1).random()
                    c=(0..column-1).random()}
            }
            while(!setMine(r,c) )/////////////////If thw mine was already set up on this place then we generate more numbers
            {
                r=(0..row-1).random()
                c=(0..column-1).random()
                if(x==r && y==c)
                {
                    while(x==r && y==c)
                    {r=(0..row-1).random()
                    c=(0..column-1).random()}
                }
            }
        }

    }



    private fun setMine(rowl: Int, columnl: Int) : Boolean{/////////////////Function to set mine
        if(board[rowl][columnl].value != MINE) {
            count--
            board[rowl][columnl].value = MINE
            updateNeighbours(rowl,columnl)
            return true
        }
        return false
    }

    //updates the values os the cells neighbouring to the mines
    private fun updateNeighbours(rowl: Int, columnl: Int) {
        for (i in movement) {
            for (j in movement) {
                if(((rowl+i) in 0 until row) && ((columnl+j) in 0 until column) && board[rowl+i][columnl+j].value != MINE)
                    board[rowl+i][columnl+j].value++
            }
        }
    }
    private fun move(view: View) {////////////Move function when user wants to open cell
        val b = view as ImageButton
        val x = b.id / 10
        val y = b.id % 10



        if (board[x][y].isMarked)
            return
        if (board[x][y].isRevealed)
            return
        if (board[x][y].value == MINE) {
            status = Status.LOST
            showMines()
            return
        }
        count--
        board[x][y].isRevealed = true

        b.isEnabled=false
        when(board[x][y].value)
        {
            1->{
                b.setScaleType(ImageView.ScaleType.FIT_CENTER)
                b.setImageResource(onem)

            }
            2->{
                b.setScaleType(ImageView.ScaleType.FIT_CENTER)
                b.setImageResource(two)

            }
            3->{
                b.setScaleType(ImageView.ScaleType.FIT_CENTER)
                b.setImageResource(three)
            }
            4->{
                b.setScaleType(ImageView.ScaleType.FIT_CENTER)
                b.setImageResource(four)
            }
            5->{
                b.setScaleType(ImageView.ScaleType.FIT_CENTER)
                b.setImageResource(five)
            }
            6->{
                b.setScaleType(ImageView.ScaleType.FIT_CENTER)
                b.setImageResource(six)
            }
            7->{
                b.setScaleType(ImageView.ScaleType.FIT_CENTER)
                b.setImageResource(sevensmall)
            }
            8->
            {
                b.setScaleType(ImageView.ScaleType.FIT_CENTER)
                b.setImageResource(eight)
            }
        }
        val gd = GradientDrawable()
        gd.cornerRadius = 5f
        gd.setStroke(3, Color.BLACK)
        gd.setColor(Color.parseColor("#797E4242"))
        b.background=gd
        if (count == 0) {
            status = Status.WON
            show()
            return
        }
        if (board[x][y].value == 0 ) {
            zero(x, y)// if there are no mines around that cell then zero() is called
            if (status == Status.WON)
            {
                show()
                return
            }
        }




        return

    }
    private fun zero(x:Int,y:Int)///Recursive function to reveal all cells around a particular cell with 0 adjacent mines
    {
        if(x<0 || x>row-1 || y<0 || y>column-1)
            return
        if(board[x][y].value==MINE) {
            return
        }
        if(status==Status.WON)
    {    show()
        return}


        for(i in 0..7)

        {

            var j=x
            var k=y
            val r=xDir[i]
            val c=yDir[i]
            j=j+r
            k=k+c

            while(j>=0 && j<row && k>=0 && k<column)
            {
                if(board[j][k].value==MINE)
                {
                    j=j+r
                    k=k+c
                    continue;
                }
                if(board[j][k].isRevealed)
                {
                    break}
                if(board[j][k].isMarked)
                    board[j][k].isMarked=false
                board[j][k].isRevealed=true
                val b:ImageButton=findViewById(j*10+k)
                b.isEnabled=false
                val gd = GradientDrawable()
                gd.cornerRadius = 5f
                gd.setStroke(3, Color.BLACK)
                gd.setColor(Color.parseColor("#797E4242"))
                b.background=gd

                if(board[j][k].value!=0)
                    when(board[j][k].value)
                    {
                        1->{

                            b.setImageResource(onem)
                            b.setScaleType(ImageView.ScaleType.FIT_CENTER)

                        }
                        2->{
                            b.setScaleType(ImageView.ScaleType.FIT_CENTER)
                            b.setImageResource(two)

                        }
                        3->{
                            b.setScaleType(ImageView.ScaleType.FIT_CENTER)
                            b.setImageResource(three)
                        }
                        4->{
                            b.setScaleType(ImageView.ScaleType.FIT_CENTER)
                            b.setImageResource(four)
                        }
                        5->{
                            b.setScaleType(ImageView.ScaleType.FIT_CENTER)
                            b.setImageResource(five)
                        }
                        6->{
                            b.setScaleType(ImageView.ScaleType.FIT_CENTER)
                            b.setImageResource(six)
                        }
                        7->{
                            b.setScaleType(ImageView.ScaleType.FIT_CENTER)
                            b.setImageResource(sevensmall)
                        }
                        8->
                        {
                            b.setScaleType(ImageView.ScaleType.FIT_CENTER)
                            b.setImageResource(eight)
                        }
                    }
                count--
                if(board[j][k].value==0)
                    zero(j,k)

                if(count==0)
                {
                    status=Status.WON
                    show()
                    return
                }
                if(board[j][k].value!=0)
                    break
                j=j+r
                k=k+c

            }
        }}

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun flag(view:View):Boolean///////////Move function when user wants to flag a cell
    {
        val b=view as ImageButton
        val x=b.id/10
        val y=b.id%10
        board[x][y].isMarked=!board[x][y].isMarked

        if(board[x][y].isMarked)
        {
            minesv.text=(minesv.text.toString().trim().toInt()-1).toString()
            if(board[x][y].value==MINE)
        {
            f--
        }

            if(f==0)
            {
                show()
                return false
            }
            b.isClickable=false
            b.isLongClickable=true

            b.setImageResource(realflagminesweeper)
            b.setScaleType(ImageView.ScaleType.CENTER_INSIDE)

        }
        else
        {   minesv.text=(minesv.text.toString().trim().toInt()+1).toString()
            b.setImageResource(android.R.color.transparent)
            if(board[x][y].value==MINE)
        {
            f++;
        }

            b.isLongClickable=true


            

        }


        return false

    }
    private fun show()///////////////Function called when game ends and user wins, reveals all the cells
    {   status=Status.WON
        updateScore()
        for(i in 0..row-1)
        {
            for(j in 0..column-1)
            {
                val b:ImageButton=findViewById(i*10+j)
                if(board[i][j].value==MINE)
                {
                    b.setScaleType(ImageView.ScaleType.FIT_CENTER)
                    b.setImageResource(actualmine)
                    b.isEnabled=false
                }
                else
                {

                when(board[i][j].value)//Setting numbers to image buttons accordingly
                {
                   1->{

                       b.setImageResource(onem)
                       b.setScaleType(ImageView.ScaleType.FIT_CENTER)

                   }
                    2->{
                        b.setScaleType(ImageView.ScaleType.FIT_CENTER)
                        b.setImageResource(two)

                    }
                    3->{
                        b.setScaleType(ImageView.ScaleType.FIT_CENTER)
                        b.setImageResource(three)
                    }
                    4->{
                        b.setScaleType(ImageView.ScaleType.FIT_CENTER)
                        b.setImageResource(four)
                    }
                    5->{
                        b.setScaleType(ImageView.ScaleType.FIT_CENTER)
                        b.setImageResource(five)
                    }
                    6->{
                        b.setScaleType(ImageView.ScaleType.FIT_CENTER)
                        b.setImageResource(six)
                    }
                    7->{
                        b.setScaleType(ImageView.ScaleType.FIT_CENTER)
                        b.setImageResource(sevensmall)
                    }
                    8->
                    {
                        b.setScaleType(ImageView.ScaleType.FIT_CENTER)
                        b.setImageResource(eight)
                    }
                }
                b.isEnabled=false
                    val gd = GradientDrawable()
                    gd.cornerRadius = 5f
                    gd.setStroke(3, Color.BLACK)
                    gd.setColor(Color.parseColor("#797E4242"))
                    b.background=gd


                }
            }
        }
        Toast.makeText(this,"Congratulations! You won", Toast.LENGTH_SHORT).show()



    }
    private fun showMines()//////////////////Function called when user clicks a mine , reveals all the mines
    {   status=Status.LOST
          chronometer.stop()
        for(i in 0..row-1)
        {
            for(j in 0..column-1)
            {
                val b:ImageButton=findViewById(i*10+j)
                if(board[i][j].value==MINE)
                {

                    b.setScaleType(ImageView.ScaleType.CENTER_INSIDE)
                    b.setImageResource(actualmine)
                }
                b.isEnabled=false
            }
        }
        Toast.makeText(this,"You lost,try again", Toast.LENGTH_SHORT).show()
    }
    private fun updateScore() {
        chronometer.stop()

        // Getting elapsed time from chronometer
        val elapsedTime = SystemClock.elapsedRealtime() - this.chronometer.base;
        val sharedPref = getSharedPreferences("Minesweeper", Context.MODE_PRIVATE)
        val lastTime = elapsedTime.toInt()

        // Setting up highscore
        var highScore = sharedPref.getInt("High Score", Integer.MAX_VALUE)


        // Comparing high score
        val temp = highScore
        if (lastTime < highScore) {
            highScore = lastTime

        }

        with(sharedPref.edit()) {
            putInt("High Score", highScore)
            putInt("Last Time", lastTime)
            commit()
        }
        Handler().postDelayed(Runnable {
            if (lastTime < temp) {
                Toast.makeText(
                    this,
                    "Congratulations! You set a new time record. Use share button to share your best time via text message",
                    Toast.LENGTH_LONG
                ).show()
                val share: Button = findViewById(R.id.Share)
                share.isVisible = true;

                share.setOnClickListener {
                    val intent = Intent(this, Share::class.java)//Intent for message activity
                    if(ActivityCompat.checkSelfPermission(this,Manifest.permission.SEND_SMS)==PackageManager.PERMISSION_GRANTED)//////////Checking if messaging permission is granted or not
                        startActivity(intent)
                   else {
                        Toast.makeText(this,"Press the share button again after granting permission", Toast.LENGTH_SHORT).show()////////Alerting if permisiion is still not gramted then asking for it
                        Handler().postDelayed(Runnable {ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), 0)}, 1000)

                    }

                }
            }
        }, 1000)
    }}










