package com.zarar.recruiterportfolio

import android.content.ContentValues
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var db: PortfolioDbHelper
    private lateinit var content: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = PortfolioDbHelper(this)
        setContentView(R.layout.activity_main)
        content = findViewById(R.id.content)
        showHome()
    }

    private fun showHome() {
        content.removeAllViews()
        addTitle("Mohammad Zarar Zafar", "Android Developer • Kotlin • Java • AI/ML")
        addText("2+ years building Android applications with clean architecture, modern UI and practical problem solving.")
        addButton("🚀 Featured Projects") { showProjects(true) }
        addButton("🧠 Skills & Expertise") { showSkills() }
        addButton("📊 Career Dashboard") { showDashboard() }
        addButton("💼 Recruiter View") { showRecruiter() }
        addButton("✉ Contact / Hire Me") { showContact() }
        addButton("⚙ Settings & Data") { showData() }
    }

    private fun showProjects(featuredOnly: Boolean = false) {
        content.removeAllViews(); addTitle("Projects", "Real projects with technologies and impact")
        val c = db.readableDatabase
        val where = if (featuredOnly) " WHERE featured=1" else ""
        c.rawQuery("SELECT title,tech,description,github FROM projects$where ORDER BY id DESC", null).use { cur ->
            while (cur.moveToNext()) addCard("${cur.getString(0)}\n\n${cur.getString(1)}\n\n${cur.getString(2)}")
        }
        addButton("＋ Add Project") { addProject() }; addButton("← Home") { showHome() }
    }

    private fun showSkills() {
        content.removeAllViews(); addTitle("Skills", "Technical strengths")
        db.readableDatabase.rawQuery("SELECT name,level FROM skills ORDER BY level DESC", null).use { cur ->
            while (cur.moveToNext()) addCard("${cur.getString(0)}  •  ${cur.getInt(1)}%")
        }
        addButton("← Home") { showHome() }
    }

    private fun showDashboard() {
        content.removeAllViews(); addTitle("Career Dashboard", "A recruiter-friendly snapshot")
        val projects = db.readableDatabase.rawQuery("SELECT COUNT(*) FROM projects", null).use { it.moveToFirst(); it.getInt(0) }
        val skills = db.readableDatabase.rawQuery("SELECT COUNT(*) FROM skills", null).use { it.moveToFirst(); it.getInt(0) }
        val messages = db.readableDatabase.rawQuery("SELECT COUNT(*) FROM messages", null).use { it.moveToFirst(); it.getInt(0) }
        addCard("Projects\n$projects")
        addCard("Skills\n$skills")
        addCard("Saved recruiter messages\n$messages")
        addCard("Focus\nAndroid • Kotlin • Clean UI • Local data • AI/ML")
        addButton("← Home") { showHome() }
    }

    private fun showRecruiter() {
        content.removeAllViews(); addTitle("Why Hire Me?", "Fast learner • Product mindset • Mobile focused")
        listOf("Modern Kotlin Android development", "Material 3 UI and responsive layouts", "SQLite persistence and CRUD", "Git/GitHub workflow", "Algorithms and problem solving", "AI/ML integration interest", "Portfolio projects with clear documentation").forEach { addCard("✓ $it") }
        addButton("View Projects") { showProjects(false) }; addButton("Contact") { showContact() }; addButton("← Home") { showHome() }
    }

    private fun showContact() {
        content.removeAllViews(); addTitle("Contact", "Send a message")
        val name = EditText(this); name.hint = "Your name"; content.addView(name)
        val email = EditText(this); email.hint = "Your email"; content.addView(email)
        val msg = EditText(this); msg.hint = "Message"; msg.minLines = 4; content.addView(msg)
        addButton("Save Message") {
            val cv = ContentValues().apply { put("name", name.text.toString()); put("email", email.text.toString()); put("message", msg.text.toString()); put("created_at", System.currentTimeMillis()) }
            db.writableDatabase.insert("messages", null, cv); Toast.makeText(this, "Message saved", Toast.LENGTH_SHORT).show(); showHome()
        }
        addButton("← Home") { showHome() }
    }

    private fun addProject() {
        val title = EditText(this); title.hint = "Project title"
        val tech = EditText(this); tech.hint = "Technologies"
        val desc = EditText(this); desc.hint = "Description"; desc.minLines = 3
        content.addView(title); content.addView(tech); content.addView(desc)
        addButton("Save Project") {
            val cv = ContentValues().apply { put("title", title.text.toString()); put("tech", tech.text.toString()); put("description", desc.text.toString()); put("github", ""); put("featured", 0) }
            db.writableDatabase.insert("projects", null, cv); showProjects(false)
        }
    }

    private fun showData() { content.removeAllViews(); addTitle("Settings & Data", "Local SQLite database")
        addButton("View All Projects") { showProjects(false) }
        addButton("Delete All Messages") { db.writableDatabase.delete("messages", null, null); Toast.makeText(this,"Messages cleared",Toast.LENGTH_SHORT).show() }
        addButton("← Home") { showHome() }
    }

    private fun addTitle(a: String, b: String) { val t = TextView(this); t.text = "$a\n$b"; t.textSize = 26f; t.setPadding(8,20,8,20); content.addView(t) }
    private fun addText(s: String) { val t=TextView(this); t.text=s; t.textSize=17f; t.setPadding(8,8,8,16); content.addView(t) }
    private fun addCard(s: String) { val t=TextView(this); t.text=s; t.textSize=16f; t.setPadding(20,18,20,18); content.addView(t) }
    private fun addButton(s: String, action: () -> Unit) { Button(this).apply { text=s; setOnClickListener{action()}; content.addView(this) } }
}
