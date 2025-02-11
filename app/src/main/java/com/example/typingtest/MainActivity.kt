package com.example.typingtest

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.typingtest.ui.theme.TypingTestTheme
import kotlinx.coroutines.delay
import org.xmlpull.v1.XmlPullParser

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TypingTestTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen(){
    var context = LocalContext.current
    var words = remember { loadWords(context).toMutableList() }
    var inputWord by remember { mutableStateOf("") }

    var wordsCompleted by remember { mutableStateOf(0) }
    var secondsFromStart by remember { mutableStateOf(0) }
    var startTyping by remember { mutableStateOf(false) }
    var wpm = if (secondsFromStart!=0) 1.0*wordsCompleted/secondsFromStart else 0.0

    LaunchedEffect(startTyping) {
        while (true){
            delay(1000L)
            secondsFromStart++
        }
    }

    LaunchedEffect(Unit) {
        while (true){
            delay(5000L)
            var newWords = loadWords(context)
            if (newWords.isNotEmpty()){
                words.clear()
                words.addAll(newWords.shuffled())
            }
        }
    }
    Row {
        LazyColumn (
            modifier = Modifier
                .padding(16.dp)
                .fillMaxHeight()
        ){
            items(words){word ->
                Text(
                    text = word,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
        Column (
            modifier = Modifier
                .padding(16.dp)
                .fillMaxHeight()
        ){
            Text(text = "Your WPM: "+wpm,
                modifier = Modifier.padding(4.dp),
                fontSize = 20.sp)
            //The TextField is written with the help of ChatGPT
            TextField(
                value = inputWord,
                onValueChange = {newWord ->
                    inputWord = newWord
                    startTyping = true
                    if (words.contains(newWord)){
                        words.remove(newWord)
                        wordsCompleted++
                        inputWord = ""

                        var newWords = loadWords(context)
                            if (newWords.isNotEmpty()){
                                words.add(newWords.random())     //by ChatGPT
                            }
                    }
                },
                label = { Text("Type here") }
            )
        }
    }

}
fun loadWords(context: Context):List<String>{
    var words = mutableListOf<String>()
    val parser = context.resources.getXml(R.xml.typingwords)

    while (parser.next() != XmlPullParser.END_DOCUMENT) {
        if (parser.eventType == XmlPullParser.START_TAG && parser.name == "word") {
            var word = ""
            word = parser.nextText()
            words.add(word)
        }
    }
    parser.close()
    return words
}