import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.jodatime.date
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

// schema definition
object ExposedDepartments : IntIdTable("t_department") {
    val name = varchar("name", 50)
    val location = varchar("location", 50)
}

object ExposedEmployees : IntIdTable("t_employee") {
    val name = varchar("name", 50)
    val job = varchar("job", 50).nullable()
    val hireDate = date("hire_date")
    val salary = long("salary")
    val departmentId = reference("department_id", ExposedDepartments.id)
}

class ExposedDepartment(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ExposedDepartment>(ExposedDepartments)
    var name by ExposedDepartments.name
    var location by ExposedDepartments.location
}

class ExposedEmployee(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ExposedEmployee>(ExposedEmployees)
    var name by ExposedEmployees.name
    var job by ExposedEmployees.job
    var hireDate by ExposedEmployees.hireDate
    var salary by ExposedEmployees.salary
    var department by ExposedDepartment referencedOn ExposedEmployees.departmentId
}


fun main(args: Array<String>) {
    // connect
    val database = Database.connect("jdbc:mysql://localhost:3306/sample_db", user = "root", password = "root")

    // use transaction
    transaction(database) {  }

    // DSL
    transaction(database) {
        // print sql to std-out
        addLogger(StdOutSqlLogger)
        // select
        ExposedEmployees
            .slice(ExposedEmployees.name)
            .select { (ExposedEmployees.departmentId eq 1) and (ExposedEmployees.name like "%郎%") }.forEach {  }
        // aggregation
        ExposedEmployees
            .slice(ExposedEmployees.departmentId, ExposedEmployees.salary.avg())
            .selectAll()
            .groupBy(ExposedEmployees.departmentId)
            .having { ExposedEmployees.salary.avg() greater 100.0 }.forEach {  }
        // union
        ExposedEmployees
            .slice(ExposedEmployees.id)
            .selectAll()
            .union(ExposedDepartments.slice(ExposedDepartments.id).selectAll())
            .forEach {  }
        // join
        ExposedEmployees
            .join(ExposedDepartments, JoinType.LEFT)
            .slice(ExposedEmployees.name, ExposedDepartments.name)
            .selectAll()
            .orderBy(ExposedEmployees.id to SortOrder.ASC)
            .forEach {  }
        // insert
        ExposedEmployees.insert {
            it[name] =  "Server-side 三郎"
            it[job] =  "Fullstack Engineer"
            it[hireDate] = DateTime.now()
            it[salary] =  10000
            it[departmentId] =  1
        }
        // update
        ExposedEmployees.update({ ExposedEmployees.id eq 2 }) {
            it[salary] = 100
        }
        // delete
        ExposedEmployees.deleteWhere { ExposedEmployees.id eq 4 }
    }

    // DAO
    transaction {
        // create
        val department = ExposedDepartment.new {
            name = "Sales"
            location = "Osaka"
        }
        println(department.location)
        // update
        department.location = "Fukuoka"
        println(department.location)
        // read
        ExposedDepartment.findById(department.id)?.also {
            println(it.location)
        }
        // delete
        department.delete()
        ExposedDepartment.findById(department.id)?.also {
            println(it.location)
        } ?: println("Deleted")
    }
}