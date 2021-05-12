@file:Suppress("PackageDirectoryMismatch")

package komapper

import org.komapper.annotation.KmAutoIncrement
import org.komapper.annotation.KmEntity
import org.komapper.annotation.KmId
import org.komapper.annotation.KmTable
import org.komapper.core.Database
import org.komapper.core.dsl.*
import org.komapper.transaction.transaction
import java.time.LocalDate

@KmEntity
@KmTable("t_department")
data class KomapperDepartment(
    @KmId @KmAutoIncrement val id: Int = 0,
    var name: String,
    var location: String,
) {
    companion object
}

@KmEntity
@KmTable("t_employee")
data class KomapperEmployee(
    @KmId @KmAutoIncrement val id: Int = 0,
    var name: String,
    var job: String,
    var hireDate: LocalDate,
    var salary: Long,
    var departmentId: Int
) {
    companion object
}

fun main() {
    val database = Database.create("jdbc:mysql://localhost:3306/sample_db", user = "root", password = "root")

    val e = KomapperEmployee.meta
    val d = KomapperDepartment.meta

    // SQL DSL
    database.transaction {
        // select
        val select = SqlDsl.from(e)
            .where {
                e.departmentId notEq 4
                e.name like "%郎%"
            }
            .select(e.name)
        database.runQuery { select }

        // aggregation
        val aggregation = SqlDsl.from(e)
            .groupBy(e.departmentId)
            .having { avg(e.salary) greater 100.0 }
            .select(e.departmentId, avg(e.salary))
        database.runQuery { aggregation }

        // union
        val union = SqlDsl.from(e).select(e.id) unionAll
                SqlDsl.from(d).select(d.id)
        database.runQuery { union }

        // join
        val join = SqlDsl.from(e)
            .leftJoin(d) {
                e.departmentId eq d.id
            }
            .orderBy(e.id.asc())
            .select(e.name, d.name)
        database.runQuery { join }
    }

    // SQL DSL
    database.transaction {
        // insert
        val insert = SqlDsl.insert(e).values {
            e.name set "Server-side 三郎"
            e.job set "Fullstack"
            e.hireDate set LocalDate.now()
            e.salary set 10000
            e.departmentId set 1
        }
        database.runQuery { insert }

        // update
        val update = SqlDsl.update(e).set {
            e.salary set 100
        }.where {
            e.id eq 2
        }
        database.runQuery { update }

        // delete
        val delete = SqlDsl.delete(e).where {
            e.id eq 4
        }
        database.runQuery { delete }
    }

    // Entity DSL
    database.transaction {
        // create
        val department = database.runQuery {
            EntityDsl.insert(d).single(
                KomapperDepartment(
                    name = "Sales",
                    location = "Osaka"
                )
            )
        }
        println(department.location)

        // update
        val department2 = database.runQuery {
            department.location = "Fukuoka"
            EntityDsl.update(d).single(department)
        }
        println(department2.location)

        // read
        database.runQuery {
            EntityDsl.from(d).first { d.id eq department2.id }
        }.also {
            println(it.location)
        }

        // delete
        database.runQuery {
            EntityDsl.delete(d).single(department2) +
                    EntityDsl.from(d).firstOrNull { d.id eq department2.id }
        }?.also {
            println(it.location)
        } ?: println("Deleted")
    }
}