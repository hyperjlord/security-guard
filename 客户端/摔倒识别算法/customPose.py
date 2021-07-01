# Copyright 2020-2021 The MediaPipe Authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

"""MediaPipe Pose."""

import enum
from typing import NamedTuple

import numpy as np

from mediapipe.calculators.core import constant_side_packet_calculator_pb2
# The following imports are needed because python pb2 silently discards
# unknown protobuf fields.
# pylint: disable=unused-import
from mediapipe.calculators.core import gate_calculator_pb2
from mediapipe.calculators.core import split_vector_calculator_pb2
from mediapipe.calculators.tensor import image_to_tensor_calculator_pb2
from mediapipe.calculators.tensor import inference_calculator_pb2
from mediapipe.calculators.tensor import tensors_to_classification_calculator_pb2
from mediapipe.calculators.tensor import tensors_to_detections_calculator_pb2
from mediapipe.calculators.tensor import tensors_to_landmarks_calculator_pb2
from mediapipe.calculators.tflite import ssd_anchors_calculator_pb2
from mediapipe.calculators.util import detections_to_rects_calculator_pb2
from mediapipe.calculators.util import landmarks_smoothing_calculator_pb2
from mediapipe.calculators.util import local_file_contents_calculator_pb2
from mediapipe.calculators.util import logic_calculator_pb2
from mediapipe.calculators.util import non_max_suppression_calculator_pb2
from mediapipe.calculators.util import rect_transformation_calculator_pb2
from mediapipe.calculators.util import thresholding_calculator_pb2
from mediapipe.calculators.util import visibility_smoothing_calculator_pb2
from mediapipe.framework.tool import switch_container_pb2
# pylint: enable=unused-import

from mediapipe.python.solution_base import SolutionBase
from mediapipe.python.solutions import download_utils


class PoseLandmark(enum.IntEnum):
  """The 33 pose landmarks."""
  NOSE = 0
  LEFT_EYE_INNER = 1
  LEFT_EYE = 2
  LEFT_EYE_OUTER = 3
  RIGHT_EYE_INNER = 4
  RIGHT_EYE = 5
  RIGHT_EYE_OUTER = 6
  LEFT_EAR = 7
  RIGHT_EAR = 8
  MOUTH_LEFT = 9
  MOUTH_RIGHT = 10
  LEFT_SHOULDER = 11
  RIGHT_SHOULDER = 12
  LEFT_ELBOW = 13
  RIGHT_ELBOW = 14
  LEFT_WRIST = 15
  RIGHT_WRIST = 16
  LEFT_PINKY = 17
  RIGHT_PINKY = 18
  LEFT_INDEX = 19
  RIGHT_INDEX = 20
  LEFT_THUMB = 21
  RIGHT_THUMB = 22
  LEFT_HIP = 23
  RIGHT_HIP = 24
  LEFT_KNEE = 25
  RIGHT_KNEE = 26
  LEFT_ANKLE = 27
  RIGHT_ANKLE = 28
  LEFT_HEEL = 29
  RIGHT_HEEL = 30
  LEFT_FOOT_INDEX = 31
  RIGHT_FOOT_INDEX = 32

BINARYPB_FILE_PATH = 'mediapipe/modules/pose_landmark/pose_landmark_cpu.binarypb'
POSE_CONNECTIONS = frozenset([
    (PoseLandmark.NOSE, PoseLandmark.RIGHT_EYE_INNER),
    (PoseLandmark.RIGHT_EYE_INNER, PoseLandmark.RIGHT_EYE),
    (PoseLandmark.RIGHT_EYE, PoseLandmark.RIGHT_EYE_OUTER),
    (PoseLandmark.RIGHT_EYE_OUTER, PoseLandmark.RIGHT_EAR),
    (PoseLandmark.NOSE, PoseLandmark.LEFT_EYE_INNER),
    (PoseLandmark.LEFT_EYE_INNER, PoseLandmark.LEFT_EYE),
    (PoseLandmark.LEFT_EYE, PoseLandmark.LEFT_EYE_OUTER),
    (PoseLandmark.LEFT_EYE_OUTER, PoseLandmark.LEFT_EAR),
    (PoseLandmark.MOUTH_RIGHT, PoseLandmark.MOUTH_LEFT),
    (PoseLandmark.RIGHT_SHOULDER, PoseLandmark.LEFT_SHOULDER),
    (PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_ELBOW),
    (PoseLandmark.RIGHT_ELBOW, PoseLandmark.RIGHT_WRIST),
    (PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_ELBOW),
    (PoseLandmark.LEFT_ELBOW, PoseLandmark.LEFT_WRIST),
    (PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_HIP),
    (PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_HIP),
    (PoseLandmark.RIGHT_HIP, PoseLandmark.LEFT_HIP),
    (PoseLandmark.RIGHT_HIP, PoseLandmark.RIGHT_KNEE),
    (PoseLandmark.LEFT_HIP, PoseLandmark.LEFT_KNEE),
    (PoseLandmark.RIGHT_KNEE, PoseLandmark.RIGHT_ANKLE),
    (PoseLandmark.LEFT_KNEE, PoseLandmark.LEFT_ANKLE),
    (PoseLandmark.RIGHT_ANKLE, PoseLandmark.RIGHT_HEEL),
    (PoseLandmark.LEFT_ANKLE, PoseLandmark.LEFT_HEEL),
    (PoseLandmark.RIGHT_HEEL, PoseLandmark.RIGHT_FOOT_INDEX),
    (PoseLandmark.LEFT_HEEL, PoseLandmark.LEFT_FOOT_INDEX),
    (PoseLandmark.RIGHT_ANKLE, PoseLandmark.RIGHT_FOOT_INDEX),
    (PoseLandmark.LEFT_ANKLE, PoseLandmark.LEFT_FOOT_INDEX),
])


def _download_oss_pose_landmark_model(model_complexity):
  """Downloads the pose landmark lite/heavy model from the MediaPipe Github repo if it doesn't exist in the package."""

  if model_complexity == 0:
    download_utils.download_oss_model(
        'mediapipe/modules/pose_landmark/pose_landmark_lite.tflite')
  elif model_complexity == 2:
    download_utils.download_oss_model(
        'mediapipe/modules/pose_landmark/pose_landmark_heavy.tflite')


class Pose(SolutionBase):
  """MediaPipe Pose.

  MediaPipe Pose processes an RGB image and returns pose landmarks on the most
  prominent person detected.

  Please refer to https://solutions.mediapipe.dev/pose#python-solution-api for
  usage examples.
  """

  def __init__(self,
               static_image_mode=False,
               model_complexity=1,
               smooth_landmarks=True,
               min_detection_confidence=0.5,
               min_tracking_confidence=0.5):
    """Initializes a MediaPipe Pose object.

    Args:
      static_image_mode: Whether to treat the input images as a batch of static
        and possibly unrelated images, or a video stream. See details in
        https://solutions.mediapipe.dev/pose#static_image_mode.
      model_complexity: Complexity of the pose landmark model: 0, 1 or 2. See
        details in https://solutions.mediapipe.dev/pose#model_complexity.
      smooth_landmarks: Whether to filter landmarks across different input
        images to reduce jitter. See details in
        https://solutions.mediapipe.dev/pose#smooth_landmarks.
      min_detection_confidence: Minimum confidence value ([0.0, 1.0]) for person
        detection to be considered successful. See details in
        https://solutions.mediapipe.dev/pose#min_detection_confidence.
      min_tracking_confidence: Minimum confidence value ([0.0, 1.0]) for the
        pose landmarks to be considered tracked successfully. See details in
        https://solutions.mediapipe.dev/pose#min_tracking_confidence.
    """
    _download_oss_pose_landmark_model(model_complexity)
    super().__init__(
        binary_graph_path=BINARYPB_FILE_PATH,
        side_inputs={
            'model_complexity': model_complexity,
            'smooth_landmarks': smooth_landmarks and not static_image_mode,
        },
        calculator_params={
            'ConstantSidePacketCalculator.packet': [
                constant_side_packet_calculator_pb2
                .ConstantSidePacketCalculatorOptions.ConstantSidePacket(
                    bool_value=not static_image_mode)
            ],
            'poselandmarkcpu__posedetectioncpu__TensorsToDetectionsCalculator.min_score_thresh':
                min_detection_confidence,
            'poselandmarkcpu__poselandmarkbyroicpu__ThresholdingCalculator.threshold':
                min_tracking_confidence,
        },
        outputs=['pose_landmarks'])

  def process(self, image: np.ndarray) -> NamedTuple:
    """Processes an RGB image and returns the pose landmarks on the most prominent person detected.

    Args:
      image: An RGB image represented as a numpy ndarray.

    Raises:
      RuntimeError: If the underlying graph throws any error.
      ValueError: If the input image is not three channel RGB.

    Returns:
      A NamedTuple object with a "pose_landmarks" field that contains the pose
      landmarks on the most prominent person detected.
    """

    results = super().process(input_data={'image': image})
    if results.pose_landmarks:
      for landmark in results.pose_landmarks.landmark:
        landmark.ClearField('presence')
    return results
