package sit.int221.BEOR2.db1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import sit.int221.BEOR2.db1.entities.Board;
import sit.int221.BEOR2.db1.entities.Status;
import sit.int221.BEOR2.db1.entities.Task;
import sit.int221.BEOR2.db1.repositories.BoardRepository;
import sit.int221.BEOR2.exceptions.ApiBadRequestException;
import sit.int221.BEOR2.exceptions.ApiNotFoundException;
import sit.int221.BEOR2.db1.repositories.StatusRepository;
import sit.int221.BEOR2.db1.repositories.TaskRepository;

import java.util.List;

@Service
public class StatusService {
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private BoardRepository boardRepository;

    public List<Status> getStatuses(@PathVariable String boardId) {
        if (!boardRepository.existsById(boardId)) {
            throw new ApiNotFoundException("Board id '" + boardId + "' not found");
        }

        return statusRepository.findByBoardId(boardId);
    }

    public Status getStatusById(@PathVariable String boardId, @PathVariable Integer statusId) {
        return statusRepository.findByBoardIdAndId(boardId, statusId)
                .orElseThrow(() -> new ApiNotFoundException("Status id '" + statusId + "' does not exist for board id '" + boardId + "'"));
    }

    @Transactional
    public Status addStatus(@PathVariable String boardId, @RequestBody Status status) {
        Board existingBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new ApiNotFoundException("Board id '" + boardId + "' not found"));

        if (!boardRepository.existsById(boardId)) {
            throw new ApiNotFoundException("Board id '" + boardId + "' not found");
        }

        status.setBoard(existingBoard);
        status.setName(boardId.substring(10) + status.getName());
        return statusRepository.saveAndFlush(status);
    }

    @Transactional
    public Status editStatus(@PathVariable String boardId,
                             @PathVariable Integer statusId,
                             @RequestBody Status status) {
        if (statusId == 1) {
            throw new ApiBadRequestException("Status id '" + statusId + "' cannot be edited");
        }

        Status existingStatus = statusRepository.findByBoardIdAndId(boardId, statusId)
                .orElseThrow(() -> new ApiNotFoundException("Status id '" + statusId + "' in board id '" + boardId + "' not found"));

        if (!statusRepository.existsById(statusId)) {
            throw new ApiNotFoundException("Status id '" + statusId + "' not found");
        }

        status.setId(existingStatus.getId());
        status.setBoard(existingStatus.getBoard());
        status.setName(status.getName());
        return statusRepository.saveAndFlush(status);
    }

    @Transactional
    public void deleteStatus(@PathVariable String boardId, @PathVariable Integer statusId) {
        if (statusId == 1) {
            throw new ApiBadRequestException("Status id '" + statusId + "' cannot be deleted");
        }

        Status statusToDelete = statusRepository.findByBoardIdAndId(boardId, statusId)
                .orElseThrow(() -> new ApiNotFoundException("Status id '" + statusId + "' does not exist for board id '" + boardId + "'"));

        statusRepository.delete(statusToDelete);
    }

    @Transactional
    public void transferStatus(@PathVariable String boardId,
                               @PathVariable Integer statusId,
                               @PathVariable Integer newStatusId) {
        if (statusId.equals(newStatusId) || statusId == 1) {
            throw new ApiBadRequestException("Status id '" + statusId + "' and new id '" + newStatusId + "' cannot be transferred");
        }

        Status deleteId = statusRepository.findByBoardIdAndId(boardId, statusId)
                .orElseThrow(() -> new ApiNotFoundException("Status id '" + statusId + "' does not exist for board id '" + boardId + "'"));
        Status transferId = statusRepository.findByBoardIdAndId(boardId, newStatusId)
                .orElseThrow(() -> new ApiNotFoundException("New status id '" + newStatusId + "' does not exist for board id '" + boardId + "'"));

        List<Task> tasks = taskRepository.findByStatus(deleteId);

        for (Task task : tasks) {
            task.setStatus(transferId);
        }

        taskRepository.saveAllAndFlush(tasks);
        statusRepository.delete(deleteId);
    }
}
