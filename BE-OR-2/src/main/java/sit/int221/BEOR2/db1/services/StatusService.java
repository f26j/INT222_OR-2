package sit.int221.BEOR2.db1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sit.int221.BEOR2.db1.components.ValidateAuth;
import sit.int221.BEOR2.db1.entities.Board;
import sit.int221.BEOR2.db1.entities.Status;
import sit.int221.BEOR2.db1.entities.Task;
import sit.int221.BEOR2.exceptions.ApiBadRequestException;
import sit.int221.BEOR2.exceptions.ApiForbiddenException;
import sit.int221.BEOR2.exceptions.ApiNotFoundException;
import sit.int221.BEOR2.db1.repositories.StatusRepository;
import sit.int221.BEOR2.db1.repositories.TaskRepository;

import java.util.List;

@Service
public class StatusService extends ValidateAuth {
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private TaskRepository taskRepository;

    public List<Status> getStatuses(String boardId) {
        Board board = validateBoardAccessForCollaborators(boardId);
        if (board.getVisibility().equals("PRIVATE")) {
            validateOwnership(board);
        }
        return statusRepository.findByBoardId(boardId);
    }

    public Status getStatusById(String boardId, Integer statusId) {
        Board board = validateBoardAccessForCollaborators(boardId);
        if (board.getVisibility().equals("PRIVATE")) {
            validateOwnership(board);
        }
        return statusRepository.findByBoardIdAndId(boardId, statusId)
                .orElseThrow(() -> new ApiNotFoundException("Status not found"));
    }

    @Transactional
    public Status addStatus(String boardId, Status status) {
        Board board = validateBoardAccessForCollaborators(boardId);

        if (!isOwnerOrWriteCollaborator(board)) {
            throw new ApiForbiddenException("Access Denied");
        }

        if (status == null) {
            throw new ApiBadRequestException("Status data is required");
        }

        if (statusRepository.existsByNameAndBoardId(status.getName().trim(), boardId)) {
            throw new ApiBadRequestException("Status name already exists on this board");
        }

        status.setBoard(board);
        return statusRepository.saveAndFlush(status);
    }

    @Transactional
    public Status editStatus(String boardId, Integer statusId, Status status) {
        Board board = validateBoardAccessForCollaborators(boardId);

        if (!isOwnerOrWriteCollaborator(board)) {
            throw new ApiForbiddenException("Access Denied");
        }

        Status existingStatus = statusRepository.findByBoardIdAndId(boardId, statusId)
                .orElseThrow(() -> new ApiNotFoundException("Status not found"));

        status.setId(existingStatus.getId());
        status.setBoard(existingStatus.getBoard());
        return statusRepository.saveAndFlush(status);
    }

    @Transactional
    public void deleteStatus(String boardId, Integer statusId) {
        Board board = validateBoardAccessForCollaborators(boardId);

        if (!isOwnerOrWriteCollaborator(board)) {
            throw new ApiForbiddenException("Access Denied");
        }

        Status status = statusRepository.findByBoardIdAndId(boardId, statusId)
                .orElseThrow(() -> new ApiNotFoundException("Status not found"));

        if (status.getName().equalsIgnoreCase("No Status") || status.getName().equalsIgnoreCase("Done")) {
            throw new ApiBadRequestException("The status cannot be deleted");
        }

        statusRepository.delete(status);
    }

    @Transactional
    public void transferStatus(String boardId, Integer statusId, Integer newStatusId) {
        Board board = validateBoardAccessForCollaborators(boardId);

        if (!isOwnerOrWriteCollaborator(board)) {
            throw new ApiForbiddenException("Access Denied");
        }

        if (statusId.equals(newStatusId)) {
            throw new ApiBadRequestException("Cannot transfer tasks to the same status");
        }

        Status oldStatus = statusRepository.findByBoardIdAndId(boardId, statusId)
                .orElseThrow(() -> new ApiNotFoundException("Old status not found"));

        Status newStatus = statusRepository.findByBoardIdAndId(boardId, newStatusId)
                .orElseThrow(() -> new ApiNotFoundException("New status not found"));

        List<Task> tasks = taskRepository.findByStatus(oldStatus);
        for (Task task : tasks) {
            task.setStatus(newStatus);
        }

        taskRepository.saveAllAndFlush(tasks);
        statusRepository.delete(oldStatus);
    }
}
