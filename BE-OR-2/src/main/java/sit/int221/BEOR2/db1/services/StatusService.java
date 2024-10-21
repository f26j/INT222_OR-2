package sit.int221.BEOR2.db1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sit.int221.BEOR2.db1.entities.Board;
import sit.int221.BEOR2.db1.entities.Status;
import sit.int221.BEOR2.db1.entities.Task;
import sit.int221.BEOR2.db1.entities.User;
import sit.int221.BEOR2.db1.repositories.BoardRepository;
import sit.int221.BEOR2.db1.repositories.UserRepository;
import sit.int221.BEOR2.exceptions.ApiBadRequestException;
import sit.int221.BEOR2.exceptions.ApiForbiddenException;
import sit.int221.BEOR2.exceptions.ApiNotFoundException;
import sit.int221.BEOR2.db1.repositories.StatusRepository;
import sit.int221.BEOR2.db1.repositories.TaskRepository;

import java.util.List;

//@Service
//public class StatusService {
//    @Autowired
//    private StatusRepository statusRepository;
//    @Autowired
//    private TaskRepository taskRepository;
//    @Autowired
//    private BoardRepository boardRepository;
//    @Autowired
//    private UserRepository userRepository;
//
//    // ดึงสถานะทั้งหมดของ Board ที่ระบุ
//    public List<Status> getStatuses(String boardId) {
//        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
//        System.out.println("Status" + userName);
//        User user = userRepository.findByUsername(userName).orElse(null);
//
//        Board board = boardRepository.findById(boardId)
//                .orElseThrow(() -> new ApiNotFoundException("Board id '" + boardId + "' not found"));
//
//        if (user == null && board.getVisibility().equals("PRIVATE")) {
//            throw new ApiForbiddenException("Access Denied");
//        } else {
//            checkBoardAccess(boardId);
//        }
//
//        return statusRepository.findByBoardId(boardId);
//    }
//
//    // ดึงสถานะเฉพาะเจาะจงโดยใช้ boardId และ statusId
//    public Status getStatusById(String boardId, Integer statusId) {
//        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
//        User user = userRepository.findByUsername(userName).orElse(null);
//        System.out.println("Status" + userName);
//        Board board = boardRepository.findById(boardId)
//                .orElseThrow(() -> new ApiNotFoundException("Board id '" + boardId + "' not found"));
//
//        if (user == null && board.getVisibility().equals("PRIVATE")) {
//            throw new ApiForbiddenException("Access Denied");
//        } else {
//            checkBoardAccess(boardId);
//        }
//
//        // Step 2: ตรวจสอบว่า Status มีอยู่ใน board ที่ระบุหรือไม่ (404)
//        Status status = statusRepository.findByBoardIdAndId(boardId, statusId)
//                .orElseThrow(() -> new ApiNotFoundException("Status id '" + statusId + "' does not exist for board id '" + boardId + "'"));
//
//        // Step 3: การตอบกลับปกติ (200)
//        return status;
//    }
//
//    // เพิ่ม Status ใหม่ใน Board ที่กำหนด
//    @Transactional
//    public Status addStatus(String boardId, Status status) {
//        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
//        User user = userRepository.findByUsername(userName).orElse(null);
//
//        Board existingBoard = boardRepository.findById(boardId)
//                .orElseThrow(() -> new ApiNotFoundException("Board id '" + boardId + "' not found"));
//
//        boolean isOwner = existingBoard.getUser().getOid().equals(user.getOid());
//        if (user == null && existingBoard.getVisibility().equals("PRIVATE") || !isOwner) {
//            throw new ApiForbiddenException("Access Denied");
//        } else {
//            checkBoardAccess(boardId);
//        }
//
//        if (status == null) {
//            throw new ApiBadRequestException("Nobody Know");
//        }
//
//        // Step 2: ตรวจสอบว่า Status ซ้ำหรือไม่ (400 Bad Request)
//        if (statusRepository.existsByNameAndBoardId(status.getName().trim(), boardId)) {
//            throw new ApiBadRequestException("Status name already exists on this board");
//        }
//
//        status.setBoard(existingBoard);
//        status.setName(status.getName().trim());  // Trim name for consistency
//        return statusRepository.saveAndFlush(status);
//    }
//
//    @Transactional
//    public Status editStatus(String boardId, Integer statusId, Status status) {
//        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
//        User user = userRepository.findByUsername(userName).orElse(null);
//
//        Board board = boardRepository.findById(boardId)
//                .orElseThrow(() -> new ApiNotFoundException("Board id '" + boardId + "' not found"));
//
//        boolean isOwner = board.getUser().getOid().equals(user.getOid());
//        if (user == null && board.getVisibility().equals("PRIVATE") || !isOwner) {
//            throw new ApiForbiddenException("Access Denied");
//        } else {
//            checkBoardAccess(boardId);
//        }
//
//        if (status == null) {
//            throw new ApiBadRequestException("Nobody Know");
//        }
//
//        Status existingStatus = statusRepository.findByBoardIdAndId(boardId, statusId)
//                .orElseThrow(() -> new ApiNotFoundException("Status id '" + statusId + "' in board id '" + boardId + "' not found"));
//
//        status.setId(existingStatus.getId());
//        status.setBoard(existingStatus.getBoard());
//        status.setName(status.getName().trim());
//        return statusRepository.saveAndFlush(status);
//    }
//
//    @Transactional
//    public void deleteStatus(String boardId, Integer statusId) {
//        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
//        User user = userRepository.findByUsername(userName).orElse(null);
//
//        Board board = boardRepository.findById(boardId)
//                .orElseThrow(() -> new ApiNotFoundException("Board id '" + boardId + "' not found"));
//
//        boolean isOwner = board.getUser().getOid().equals(user.getOid());
//        if (user == null && board.getVisibility().equals("PRIVATE") || !isOwner) {
//            throw new ApiForbiddenException("Access Denied");
//        } else {
//            checkBoardAccess(boardId);
//        }
//
//        Status statusToDelete = statusRepository.findByBoardIdAndId(boardId, statusId)
//                .orElseThrow(() -> new ApiNotFoundException("Status id '" + statusId + "' does not exist for board id '" + boardId + "'"));
//
//        if (statusToDelete.getName().equalsIgnoreCase("No Status") || statusToDelete.getName().equalsIgnoreCase("Done")) {
//            throw new ApiBadRequestException("The status '" + statusToDelete.getName() + "' cannot be deleted");
//        }
//
//        statusRepository.delete(statusToDelete);
//    }
//
//    @Transactional
//    public void transferStatus(String boardId, Integer statusId, Integer newStatusId) {
//        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
//        User user = userRepository.findByUsername(userName).orElse(null);
//
//        Board board = boardRepository.findById(boardId)
//                .orElseThrow(() -> new ApiNotFoundException("Board id '" + boardId + "' not found"));
//
//        boolean isOwner = board.getUser().getOid().equals(user.getOid());
//        if (user == null && board.getVisibility().equals("PRIVATE") || !isOwner) {
//            throw new ApiForbiddenException("Access Denied");
//        } else {
//            checkBoardAccess(boardId);
//        }
//
//        if (statusId.equals(newStatusId)) {
//            throw new ApiBadRequestException("Cannot transfer tasks to the same status");
//        }
//
//        Status oldStatus = statusRepository.findByBoardIdAndId(boardId, statusId)
//                .orElseThrow(() -> new ApiNotFoundException("Old status id '" + statusId + "' does not exist for board id '" + boardId + "'"));
//
//        Status newStatus = statusRepository.findByBoardIdAndId(boardId, newStatusId)
//                .orElseThrow(() -> new ApiNotFoundException("New status id '" + newStatusId + "' does not exist for board id '" + boardId + "'"));
//
//        List<Task> tasks = taskRepository.findByStatus(oldStatus);
//        for (Task task : tasks) {
//            task.setStatus(newStatus);
//        }
//
//        taskRepository.saveAllAndFlush(tasks);
//        statusRepository.delete(oldStatus);
//    }
//
//    private void checkBoardAccess(String boardId) {
//        Board board = boardRepository.findById(boardId)
//                .orElseThrow(() -> new ApiNotFoundException("Board id '" + boardId + "' not found"));
//
//        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
//        System.out.println(userName + "Status");
//
//        User user = userRepository.findByUsername(userName).orElse(null);
//        if (user != null) {
//            boolean isOwner = board.getUser().getOid().equals(user.getOid());
//            boolean isPublic = "PUBLIC".equalsIgnoreCase(board.getVisibility());
//
//            // ถ้าไม่ใช่เจ้าของและบอร์ดเป็น private ให้ส่ง 403 Forbidden
//            if (!isOwner && !isPublic) {
//                throw new ApiForbiddenException("Access denied: You do not have permission to access this board");
//            }
//        }
//    }
//}

@Service
public class StatusService {
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private UserRepository userRepository;

    public List<Status> getStatuses(String boardId) {
        Board board = validateBoardAccess(boardId);
        return statusRepository.findByBoardId(boardId);
    }

    public Status getStatusById(String boardId, Integer statusId) {
        Board board = validateBoardAccess(boardId);
        return statusRepository.findByBoardIdAndId(boardId, statusId)
                .orElseThrow(() -> new ApiNotFoundException("Status not found"));
    }

    @Transactional
    public Status addStatus(String boardId, Status status) {
        Board board = validateBoardAccess(boardId);
        validateOwnership(board);

        if (statusRepository.existsByNameAndBoardId(status.getName().trim(), boardId)) {
            throw new ApiBadRequestException("Status name already exists on this board");
        }

        status.setBoard(board);
        return statusRepository.saveAndFlush(status);
    }

    @Transactional
    public Status editStatus(String boardId, Integer statusId, Status status) {
        Board board = validateBoardAccess(boardId);
        validateOwnership(board);

        Status existingStatus = statusRepository.findByBoardIdAndId(boardId, statusId)
                .orElseThrow(() -> new ApiNotFoundException("Status not found"));

        status.setId(existingStatus.getId());
        status.setBoard(existingStatus.getBoard());
        return statusRepository.saveAndFlush(status);
    }

    @Transactional
    public void deleteStatus(String boardId, Integer statusId) {
        Board board = validateBoardAccess(boardId);
        validateOwnership(board);

        Status status = statusRepository.findByBoardIdAndId(boardId, statusId)
                .orElseThrow(() -> new ApiNotFoundException("Status not found"));

        if (status.getName().equalsIgnoreCase("No Status") || status.getName().equalsIgnoreCase("Done")) {
            throw new ApiBadRequestException("The status cannot be deleted");
        }

        statusRepository.delete(status);
    }

    @Transactional
    public void transferStatus(String boardId, Integer statusId, Integer newStatusId) {
        Board board = validateBoardAccess(boardId);
        validateOwnership(board);

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

    private Board validateBoardAccess(String boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ApiNotFoundException("Board not found"));

        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(userName).orElse(null);

        if (user == null && board.getVisibility().equals("PRIVATE")) {
            throw new ApiForbiddenException("Access Denied");
        }
        return board;
    }

    private void validateOwnership(Board board) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new ApiForbiddenException("User not found"));

        if (!board.getUser().getOid().equals(user.getOid())) {
            throw new ApiForbiddenException("You do not own this board");
        }
    }
}
