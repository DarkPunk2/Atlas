package com.project.atlas.services

import com.google.firebase.firestore.FirebaseFirestore
import com.project.atlas.interfaces.Database

class DatabaseService: Database {
    private val db = FirebaseFirestore.getInstance()

    override fun add(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getAll() {
        TODO("Not yet implemented")
    }

    override fun remove(): Boolean {
        TODO("Not yet implemented")
    }

}