import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class SubTask extends Task {

    private Epic epic;

    public SubTask(int id, String name, String description, TaskStatus taskStatus, Epic epic) {
        super(id, name, description, taskStatus);
        this.epic = epic;

        this.checkEpicSubTasks();
    }

    public SubTask(String name, String description, Epic epic) {
        super(name, description);
        this.epic = epic;

        this.checkEpicSubTasks();
    }

    /**
     * Метод выполняет проверку на наличие в связанном эпике объектов подзадач с таким же идентификатором.
     * Если они есть, то выполняется их удаление. Данная ситуация может возникнуть, если был создан новый объект
     * подзадачи с идентификатором, который уже был ранее создан (редактирование).
     *
     * Далее выполняется проверка на наличие текущей подзадачи в списке задач эпика и ее добавление
     */
    private void checkEpicSubTasks() {

        // Как мысль - если у эпика хранить не ссылки на объекты подзадач, а только список идентификаторов,
        // то это не придется делать. Но поиск привязанных подзадач у эпика будет выполняться дольше, чем когда
        // мы сразу имеем ссылки на них
        Collection<SubTask> l = this.epic.getListSubTasks().stream().filter(subTask -> subTask.getId() == this.id).
                collect(Collectors.toList());
        this.epic.getListSubTasks().removeAll(l);

        // Добавление текущей, если ее нет
        if (!this.epic.getListSubTasks().contains(epic)) {
            this.epic.addTask(this);
        }
    }

    public Epic getEpic() {
        return epic;
    }

    @Override
    public String toString() {
        return id + " | " + name + " | " + taskStatus;
    }
}
