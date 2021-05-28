package com.example.easyphone.repository

import com.example.easyphone.db.entities.ActionArg
import com.example.easyphone.db.ButtonsDatabase
import com.example.easyphone.db.entities.MyAction
import com.example.easyphone.db.entities.ProgrammedButton
import com.example.easyphone.db.utils.DbsData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ButtonsRepository(private val database: ButtonsDatabase) {
    suspend fun insertActionArg(arg: ActionArg): List<Long> {
        return withContext(Dispatchers.IO) {
            return@withContext database.actionArgDao.insertAll(arg)
        }
    }

    suspend fun insertAction(action: MyAction): List<Long> {
        return withContext(Dispatchers.IO) {
            return@withContext database.actionDao.insertAll(action)
        }
    }

    suspend fun updateButton(button: ProgrammedButton) {
        withContext(Dispatchers.IO) {
            database.programmedButtonDao.update(button)
        }
    }

    suspend fun deleteButton(id: Int) {
        withContext(Dispatchers.IO) {
            database.programmedButtonDao.deleteButtonById(id)
        }
    }

    suspend fun getButton(id: Int): ProgrammedButton {
        return withContext(Dispatchers.IO) {
            return@withContext database.programmedButtonDao.getButtonById(id)
        }
    }

    suspend fun getButtons(): List<ProgrammedButton> {
        return withContext(Dispatchers.IO) {
            return@withContext database.programmedButtonDao.getAll()
        }
    }

    suspend fun insertButton(button: ProgrammedButton): List<Long> {
        return withContext(Dispatchers.IO) {
            return@withContext database.programmedButtonDao.insertAll(button)
        }
    }

    suspend fun deleteAll(buttons: List<ProgrammedButton>) {
        withContext(Dispatchers.IO) {
            buttons.forEach {
                val a = database.programmedButtonDao.delete(it)
            }
        }
    }

    suspend fun deleteButtonsWithoutActions() {
        withContext(Dispatchers.IO) {
            database.programmedButtonDao.deleteButtonsWithoutActions()
        }
    }

    suspend fun updatePosition(id: Int, row: Int, column: Int) {
        withContext(Dispatchers.IO) {
            database.programmedButtonDao.updatePosition(id, row, column)
        }
    }

    suspend fun getActionByButtonId(buttonId: Int): MyAction {
        return withContext(Dispatchers.IO) {
            return@withContext database.actionDao.getByButtonId(buttonId)
        }
    }

    suspend fun getActionArgsByButtonId(buttonId: Int): List<ActionArg> {
        return withContext(Dispatchers.IO) {
            return@withContext database.actionArgDao.getByButtonId(buttonId)
        }
    }

    suspend fun getAllActions(): List<MyAction> {
        return withContext(Dispatchers.IO) {
            return@withContext database.actionDao.getAll()
        }
    }

    suspend fun getAllActionArgs(): List<ActionArg> {
        return withContext(Dispatchers.IO) {
            return@withContext database.actionArgDao.getAll()
        }
    }

    suspend fun clearDatabase() {
        return withContext(Dispatchers.IO) {
            database.actionArgDao.deleteAll()
            database.actionDao.deleteAll()
            database.programmedButtonDao.deleteAll()
        }
    }

    suspend fun importDatabase(dbsData: DbsData) {
        clearDatabase()
        withContext(Dispatchers.IO) {
            database.actionArgDao.insertAll(*dbsData.args.toTypedArray())
            database.actionDao.insertAll(*dbsData.actions.toTypedArray())
            database.programmedButtonDao.insertAll(*dbsData.buttons.toTypedArray())
        }
    }
}