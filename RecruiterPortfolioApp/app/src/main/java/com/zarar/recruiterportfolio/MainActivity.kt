package com.zarar.recruiterportfolio

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
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
        addText("BSCS graduate focused on building practical Android applications with clean UI, local persistence and problem-solving skills.")
        addButton("🚀 Featured Projects") { showProjects(true) }
        addButton("🧠 Skills & Expertise") { showSkills() }
        addButton("📊 Career Dashboard") { showDashboard() }
        addButton("💼 Recruiter View") { showRecruiter() }
        addButton("✉ Contact / Hire Me") { showContact() }
        addButton("⚙ Settings & Data") { showData() }
    }

    private fun showProjects(featuredOnly: Boolean = false) {
        content.removeAllViews()
        addTitle("Projects", "Android work, technologies and GitHub links")
        val where = if (featuredOnly) " WHERE featured=1" else ""
        db.readableDatabase.rawQuery(
            "SELECT title,tech,description,github FROM projects$where ORDER BY id DESC", null
        ).use { cur ->
            if (!cur.moveToFirst()) addCard("No projects found.")
            else {
                do {
                    val title = cur.getString(0)
                    val tech = cur.getString(1)
                    val description = cur.getString(2)
                    val github = cur.getString(3)
                    addCard("$title\n\n$tech\n\n$description")
                    if (!github.isNullOrBlank()) addButton("Open GitHub") { openUrl(github) }
                } while (cur.moveToNext())
            }
        }
        addButton("＋ Add Project") { addProject() }
        addButton("← Home") { showHome() }
    }

    private fun showSkills() {
        content.removeAllViews()
        addTitle("Skills", "Technical strengths")
        db.readableDatabase.rawQuery("SELECT name,level FROM skills ORDER BY level DESC", null).use { cur ->
            while (cur.moveToNext()) addCard("${cur.getString(0)}  •  ${cur.getInt(1)}%")
        }
        addButton("← Home") { showHome() }
    }

    private fun showDashboard() {
        content.removeAllViews()
        addTitle("Career Dashboard", "A recruiter-friendly snapshot")
        val projects = countRows("projects")
        val skills = countRows("skills")
        val messages = countRows("messages")
        addCard("Projects\n$projects")
        addCard("Skills\n$skills")
        addCard("Saved recruiter messages\n$messages")
        addCard("Focus\nAndroid • Kotlin • Java • Git/GitHub • AI/ML")
        addButton("← Home") { showHome() }
    }

    private fun showRecruiter() {
        content.removeAllViews()
        addTitle("Why Hire Me?", "Mobile-focused developer profile")
        listOf(
            "Kotlin and Java Android development",
            "Material UI and responsive layouts",
            "SQLite persistence and CRUD",
            "Git/GitHub workflow",
            "Algorithms and problem solving",
            "AI/ML integration interest",
            "Portfolio projects with documentation"
        ).forEach { addCard("✓ $it") }
        addButton("View Projects") { showProjects(false) }
        addButton("Contact") { showContact() }
        addButton("← Home") { showHome() }
    }

    private fun showContact() {
        content.removeAllViews()
        addTitle("Contact", "Save a recruiter message locally")
        val name = EditText(this).apply { hint = "Your name" }
        val email = EditText(this).apply { hint = "Your email"; inputType = 33 }
        val msg = EditText(this).apply { hint = "Message"; minLines = 4 }
        content.addView(name); content.addView(email); content.addView(msg)

        addButton("Save Message") {
            if (name.text.isBlank() || email.text.isBlank() || msg.text.isBlank()) {
                Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show()
                return@addButton
            }
            val cv = ContentValues().apply {
                put("name", name.text.toString().trim())
                put("email", email.text.toString().trim())
                put("message", msg.text.toString().trim())
                put("created_at", System.currentTimeMillis())
            }
            db.writableDatabase.insert("messages", null, cv)
            Toast.makeText(this, "Message saved", Toast.LENGTH_SHORT).show()
            showHome()
        }
        addButton("← Home") { showHome() }
    }

    private fun addProject() {
        content.removeAllViews()
        addTitle("Add Project", "Create a portfolio entry")
        val title = EditText(this).apply { hint = "Project title" }
        val tech = EditText(this).apply { hint = "Technologies" }
        val desc = EditText(this).apply { hint = "Description"; minLines = 3 }
        val github = EditText(this).apply { hint = "GitHub URL (optional)" }
        content.addView(title); content.addView(tech); content.addView(desc); content.addView(github)

        addButton("Save Project") {
            if (title.text.isBlank() || tech.text.isBlank() || desc.text.isBlank()) {
                Toast.makeText(this, "Title, technology and description are required", Toast.LENGTH_SHORT).show()
                return@addButton
            }
            val cv = ContentValues().apply {
                put("title", title.text.toString().trim())
                put("tech", tech.text.toString().trim())
                put("description", desc.text.toString().trim())
                put("github", github.text.toString().trim())
                put("featured", 0)
            }
            db.writableDatabase.insert("projects", null, cv)
            showProjects(false)
        }
        addButton("← Projects") { showProjects(false) }
    }

    private fun showData() {
        content.removeAllViews()
        addTitle("Settings & Data", "Local SQLite database")
        addButton("View All Projects") { showProjects(false) }
        addButton("Delete All Messages") {
            db.writableDatabase.delete("messages", null, null)
            Toast.makeText(this, "Messages cleared", Toast.LENGTH_SHORT).show()
            showData()
        }
        addButton("Open GitHub Profile") { openUrl("https://github.com/ZararZafar14") }
        addButton("← Home") { showHome() }
    }

    private fun countRows(table: String): Int =
        db.readableDatabase.rawQuery("SELECT COUNT(*) FROM $table", null).use {
            it.moveToFirst(); it.getInt(0)
        }

    private fun openUrl(url: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (_: Exception) {
            Toast.makeText(this, "Unable to open link", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addTitle(title: String, subtitle: String) {
        TextView(this).apply {
            text = "$title\n$subtitle"
            textSize = 26f
            setPadding(8, 20, 8, 20)
            content.addView(this)
        }
    }

    private fun addText(text: String) {
        TextView(this).apply {
            this.text = text
            textSize = 17f
            setPadding(8, 8, 8, 16)
            content.addView(this)
        }
    }

    private fun addCard(text: String) {
        TextView(this).apply {
            this.text = text
            textSize = 16f
            setPadding(20, 18, 20, 18)
            content.addView(this)
        }
    }

    private fun addButton(text: String, action: () -> Unit) {
        Button(this).apply {
            this.text = text
            setOnClickListener { action() }
            content.addView(this)
        }
    }
}
