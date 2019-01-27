package com.yliu240.painbutton;

import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.BoringLayout;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import pl.droidsonroids.gif.GifImageView;
import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends AppCompatActivity {

    int dmgTop,dmgBottom, critTop, critBottom;
    int[] screenCenter = new int[2];
    ImageButton sound = null;
    MediaPlayer damageFx, bgm;
    ImageView screenInFrontOfMob;
    GifImageView mob;
    RelativeLayout RL;
    RelativeLayout.LayoutParams lp;
//    Animation fadeIn, fadeOut;
//    AnimationSet animationSet;
    Shader textShader, critShader;
    Typeface comic_sans;
    final int critialDmgSize = 55;
    final int normalDmgSize = 50;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Start Bgm and Sound fx
        damageFx = MediaPlayer.create(MainActivity.this,R.raw.slime_damage_sound);
        bgm = MediaPlayer.create(MainActivity.this,R.raw.ellinia_bgm1);
        bgm.start();
        bgm.setLooping(true);

        // Sound button
        sound = (ImageButton) findViewById(R.id.sound);
        screenInFrontOfMob = (ImageView) findViewById(R.id.transparent);
        mob = (GifImageView) findViewById(R.id.slimeGif);

        // RelativeLayout holding the damageText
        RL = (RelativeLayout) findViewById(R.id.relayout);
        lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, // Width of TextView
                RelativeLayout.LayoutParams.WRAP_CONTENT); // Height of TextView


        //Colors For damageText
        dmgTop=ContextCompat.getColor(MainActivity.this, R.color.dmgTop);
        dmgBottom=ContextCompat.getColor(MainActivity.this, R.color.dmgBottom);
        critTop=ContextCompat.getColor(MainActivity.this, R.color.critTop);
        critBottom=ContextCompat.getColor(MainActivity.this, R.color.critBottom);
        comic_sans = Typeface.createFromAsset(getAssets(),"comic-sans-ms-bold.ttf");
        screenCenter[0]=this.getResources().getDisplayMetrics().widthPixels;
        screenCenter[0]-=this.getResources().getDisplayMetrics().widthPixels/2;
        screenCenter[1]=this.getResources().getDisplayMetrics().heightPixels;
        screenCenter[1]-=this.getResources().getDisplayMetrics().heightPixels/2;
    }

    @Override
    protected void onStart() {
        super.onStart();

        bgmSoundListen(); //Listens to sound Button if pressed

        screenInFrontOfMob.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch (View v, MotionEvent event){
                switch (event.getAction()) {

                    // Pressed
                    case MotionEvent.ACTION_DOWN: {
                        if(damageFx.isPlaying()){
                            damageFx.seekTo(0);
                        }
                        damageFx.start();

                        // Generate random Damage and create damage Text
                        int damageTaken=0;
                        int x = (int) event.getRawX();
                        int y = (int) event.getRawY();
                        //TODO: Make this relative to mob position
                        if (screenCenter[0]+100>x && screenCenter[0]-300<x && screenCenter[1]-50<y && screenCenter[1]+350>y){
                            damageTaken = ThreadLocalRandom.current().nextInt(500000, 999999 + 1);
                        }
                        createDamageText(damageTaken, x, y);

                        mob.setImageResource(R.drawable.kingslimehurt);
                        break;
                    }
                    // Released
                    case MotionEvent.ACTION_UP: {
                        mob.setImageResource(R.drawable.kingslime_animation);
                        break;
                    }
                }
                return true;
            }
        });
    }

    public void bgmSoundListen(){
        Boolean clicked = new Boolean(false);
        sound.setTag(clicked); // Button wasn't clicked
        sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( ((Boolean)sound.getTag())==false ){
                    sound.setImageResource(R.drawable.baseline_volume_off_24);
                    bgm.pause();
                    Toast.makeText(MainActivity.this, "Mute", Toast.LENGTH_SHORT).show();
                    sound.setTag(new Boolean(true));
                }else{
                    sound.setImageResource(R.drawable.baseline_volume_up_24);
                    bgm.start();
                    Toast.makeText(MainActivity.this, "Sound On", Toast.LENGTH_SHORT).show();
                    sound.setTag(new Boolean(false));
                }
            }
        });
    }


    // Create Damage Text and sets the font, size, text, position
    public void createDamageText(int damageTaken, float x, float y){
        int slideHeight = ThreadLocalRandom.current().nextInt(200, 400 + 1);
        int slideWidth = ThreadLocalRandom.current().nextInt(-20, 20 + 1);
        String damage = Integer.toString(damageTaken);

        final TextView damageText = new TextView(getApplicationContext());
        damageText.setLayoutParams(lp);
        damageText.setSingleLine();
        RL.addView(damageText);

        // fadeIn/Out Animations
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(10);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        fadeOut.setStartOffset(175);
        fadeOut.setDuration(350);

        TranslateAnimation moveUp = new TranslateAnimation(0, slideWidth, 0, -slideHeight);
        moveUp.setDuration(150);


        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(fadeIn);
        animationSet.addAnimation(moveUp);

        fadeOut.setAnimationListener(new TranslateAnimation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationRepeat(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                damageText.setVisibility(View.GONE);
            }
        });
        animationSet.addAnimation(fadeOut);

        damageText.setTypeface(comic_sans);
        damageText.setTextSize(normalDmgSize);
        Shader textShader=new LinearGradient(0, 0, 0, damageText.getPaint().getTextSize(),
                new int[]{dmgTop,dmgBottom},
                new float[]{0, 1}, Shader.TileMode.CLAMP);
        damageText.setShadowLayer(1.5f, 5.0f, 5.0f, Color.BLACK);

        damageText.getPaint().setShader(textShader);
        if(damageTaken>=900000){
            damageText.setTextSize(critialDmgSize);
            Shader critShader=new LinearGradient(0, 0, 0, damageText.getPaint().getTextSize(),
                    new int[]{critTop, critBottom},
                    new float[]{0, 1}, Shader.TileMode.CLAMP);
            damageText.getPaint().setShader(critShader);
        }else if (damageTaken<500000){
            damage = " MISS ";
        }
        damageText.setText(damage);
        // Position damageText to Click position
        damageText.setX(x-200);
        damageText.setY(y-150);
        damageText.setVisibility(View.VISIBLE);
        damageText.startAnimation(animationSet);
    }


    // Create AnimationSet for Damage text, returns animationSet
    public AnimationSet createDamageTextAnimation(){
        // fade Out Animations
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        fadeOut.setStartOffset(150);
        fadeOut.setDuration(400);

        int slideHeight = ThreadLocalRandom.current().nextInt(100, 200 + 1);
        int slideWidth = ThreadLocalRandom.current().nextInt(-5, 5 + 1);
        TranslateAnimation moveUp = new TranslateAnimation(0, slideWidth, 0, -slideHeight);
        moveUp.setDuration(150);
        moveUp.setStartOffset(50);

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(moveUp);

        return animationSet;
    }
}

