import java.util.Objects;

public class Task {

    private static int count = 0;

    protected final int id;
    protected String name;
    protected TaskStatus taskStatus;
    protected String description;

    /*
    Из описания: "При создании задачи менеджер присваивает ей новый идентификатор."
    Почему бы не сделать это в методе задачи, путем создания конструкторов, в котором идентификатор задается пользователем
    (для создания объектов задачи вручную с измененными данными)
    и другого конструктора, в котором идентификатор рассчитывается автоматически (см. ниже)?
     */

    public Task(int id, String name, String description, TaskStatus taskStatus) {
        this.id = id;
        this.name = name;
        this.taskStatus = taskStatus;
        this.description = description;
    }

    public Task(String name, String description) {
        this.id = count;
        this.name = name;
        this.description = description;
        this.taskStatus = TaskStatus.NEW;

        // Увеличиваем значение статического счетчика объектов для дальнейшего формирований идентификаторов
        this.count++;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id + " | " + name + " | " + taskStatus +
                "\n" + "========================================" + "\n";
    }
}
