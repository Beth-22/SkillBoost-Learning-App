const express = require("express");
const multer = require("multer");
const uploadPdf = require("../middlewares/uploadPdfMiddleware");
const upload = require("../middlewares/uploadImage");
const { uploadCourseImage, multerErrorHandler } = require("../controllers/courseController");
const { authenticateUser, authorizeRoles } = require("../middlewares/authMiddleware");
const {
  createCourse,
  getCourses,
  getCourse,
  updateCourse,
  deleteCourse,
  updateVideoInCourse,
  deleteVideoFromCourse,
  enrollInCourse,
  getEnrolledCourses,
  uploadPdfToCourse,
  searchCourses,
  getUserCourses
} = require("../controllers/courseController");

const router = express.Router();

// Apply Multer error handler to all routes
router.use(multerErrorHandler);

// Search courses
router.get("/search", searchCourses);

// Upload course image
router.post(
  "/:courseId/upload-image",
  authenticateUser,
  authorizeRoles("instructor", "admin"),
  upload.single("image"),
  uploadCourseImage
);

// Upload PDF
router.post(
  "/:id/upload/pdf",
  authenticateUser,
  authorizeRoles("instructor", "admin"),
  uploadPdf.single("pdf"),
  uploadPdfToCourse
);

// Create course
const uploadNone = multer().none();
router.post(
  "/createCourse",
  authenticateUser,
  authorizeRoles("instructor", "admin"),
  uploadNone, // Parse form-data for title and description
  createCourse
);

// Get all courses
router.get("/", authenticateUser, getCourses);

// Get single course
router.get("/:id", authenticateUser, getCourse);

// Update course
router.put("/:id", authenticateUser, updateCourse);

// Delete course
router.delete("/:id", authenticateUser, deleteCourse);

// Enroll in course
router.post("/:id/enroll", authenticateUser, enrollInCourse);

// Get enrolled courses
router.get("/student/enrolled", authenticateUser, getEnrolledCourses);

// Update video in course
router.put("/:courseId/videos/:videoId", authenticateUser, updateVideoInCourse);

// Delete video from course
router.delete("/:courseId/videos/:videoId", authenticateUser, deleteVideoFromCourse);

router.get("/user/courses", authenticateUser, authorizeRoles("instructor", "admin"), getUserCourses);

module.exports = router;