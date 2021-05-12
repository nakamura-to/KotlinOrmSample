import org.ktorm.database.Database
import org.ktorm.schema.*
import org.ktorm.dsl.*
import org.ktorm.entity.*
import java.time.LocalDate

// object definition
interface Department : Entity<Department> {
    companion object : Entity.Factory<Department>()
    val id: Int
    var name: String
    var location: String
}

interface Employee : Entity<Employee> {
    companion object : Entity.Factory<Employee>()
    val id: Int
    var name: String
    var job: String
    var hireDate: LocalDate
    var salary: Long
    var department: Department
}

// schema definition
object KtormDepartments : Table<Department>("t_department") {
    val id = int("id").primaryKey().bindTo { it.id }
    val name = varchar("name").bindTo { it.name }
    val location = varchar("location").bindTo { it.location }
}

object KtormEmployees : Table<Employee>("t_employee") {
    val id = int("id").primaryKey().bindTo { it.id }
    val name = varchar("name").bindTo { it.name }
    val job = varchar("job").bindTo { it.job }
    val hireDate = date("hire_date").bindTo { it.hireDate }
    val salary = long("salary").bindTo { it.salary }
    val departmentId = int("department_id").references(KtormDepartments) { it.department }
}

fun main() {
    // connect
    val database = Database.connect("jdbc:mysql://localhost:3306/sample_db", user = "root", password = "root")

    database.useTransaction {
        // select
        database
            .from(KtormEmployees)
            .select(KtormEmployees.name)
            .where { (KtormEmployees.departmentId notEq 4) and (KtormEmployees.name like "%郎%") }
        // aggregation
        database
            .from(KtormEmployees)
            .select(KtormEmployees.departmentId, avg(KtormEmployees.salary))
            .groupBy(KtormEmployees.departmentId)
            .having { avg(KtormEmployees.salary) greater 100.0 }
        // union
        database
            .from(KtormEmployees)
            .select(KtormEmployees.id)
            .unionAll(database.from(KtormDepartments).select(KtormDepartments.id))
        // join
        database
            .from(KtormEmployees)
            .leftJoin(KtormDepartments, on = KtormEmployees.departmentId eq KtormDepartments.id)
            .select(KtormEmployees.name, KtormDepartments.name)
            .orderBy(KtormEmployees.id.asc())
    }

    database.useTransaction {
        // insert
        database.insert(KtormEmployees) {
            set(it.name, "Server-side 三郎")
            set(it.job, "Fullstack")
            set(it.hireDate, LocalDate.now())
            set(it.salary, 10000)
            set(it.departmentId, 1)
        }
        // update
        database.update(KtormEmployees) {
            set(it.salary, 100)
            where { it.id eq 2 }
        }
        // delete
        database.delete(KtormEmployees) { it.id eq 4 }
    }

    // DAO
    database.useTransaction {
        // create
        val department = Department {
            name = "Sales"
            location = "Osaka"
        }
        println(department.location)
        database.sequenceOf(KtormDepartments).add(department)
        // update
        department.run {
            location = "Fukuoka"
            flushChanges()
        }
        println(department.location)
        // read
        database.sequenceOf(KtormDepartments).find { it.id eq department.id }?.also {
            println(it.location)
        }
        // delete
        department.delete()
        database.sequenceOf(KtormDepartments).find { it.id eq department.id }?.also {
            println(it.location)
        } ?: println("Deleted")
    }
}