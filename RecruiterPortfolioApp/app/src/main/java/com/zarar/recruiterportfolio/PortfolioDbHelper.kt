package com.zarar.recruiterportfolio

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class PortfolioDbHelper(context: Context) : SQLiteOpenHelper(context, "portfolio.db", null, 2) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE projects(id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, tech TEXT NOT NULL, description TEXT NOT NULL, github TEXT, featured INTEGER DEFAULT 0)")
        db.execSQL("CREATE TABLE skills(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, level INTEGER NOT NULL)")
        db.execSQL("CREATE TABLE messages(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, email TEXT, message TEXT, created_at INTEGER)")
        db.execSQL("CREATE TABLE settings(key TEXT PRIMARY KEY, value TEXT)")
        db.execSQL("INSERT INTO projects(title,tech,description,github,featured) VALUES('AI Fashion & Color Recommendation','Kotlin • TensorFlow Lite • ML Kit','AI-based fashion and color recommendation concept for Pakistani clothing preferences.','https://github.com/ZararZafar14/mohammadzararzafar',1)")
        db.execSQL("INSERT INTO projects(title,tech,description,github,featured) VALUES('Banker\'s Algorithm','Java • SQLite • Algorithms','Deadlock avoidance simulator with persistent local history.','https://github.com/ZararZafar14/mohammadzararzafar/tree/main/BankersAlgorithmApp',1)")
        db.execSQL("INSERT INTO projects(title,tech,description,github,featured) VALUES('Recruiter Portfolio','Kotlin • SQLite • Material','Recruiter-focused Android portfolio with projects, skills, dashboard and local contact messages.','https://github.com/ZararZafar14/mohammadzararzafar/tree/main/RecruiterPortfolioApp',1)")
        db.execSQL("INSERT INTO skills(name,level) VALUES('Kotlin',90)")
        db.execSQL("INSERT INTO skills(name,level) VALUES('Android',90)")
        db.execSQL("INSERT INTO skills(name,level) VALUES('Java',85)")
        db.execSQL("INSERT INTO skills(name,level) VALUES('Git/GitHub',80)")
        db.execSQL("INSERT INTO skills(name,level) VALUES('Firebase',75)")
        db.execSQL("INSERT INTO skills(name,level) VALUES('AI/ML',65)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS projects")
        db.execSQL("DROP TABLE IF EXISTS skills")
        db.execSQL("DROP TABLE IF EXISTS messages")
        db.execSQL("DROP TABLE IF EXISTS settings")
        onCreate(db)
    }
}
