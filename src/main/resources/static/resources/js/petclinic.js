/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

(function ($) {
  $('#birthDate').datepicker({
    format: 'yyyy/mm/dd'
  });
  $('#date').datepicker({
    format: 'yyyy/mm/dd'
  });

  // Character counter for visit description
  const visitDescriptionTextarea = document.getElementById('visitDescription');
  const visitDescriptionCharCounter = document.getElementById('visitDescriptionCharCounter');

  if (visitDescriptionTextarea && visitDescriptionCharCounter) {
    const updateCharCounter = () => {
      const maxLength = parseInt(visitDescriptionTextarea.getAttribute('maxlength'), 10);
      const currentLength = visitDescriptionTextarea.value.length;
      const remaining = maxLength - currentLength;

      visitDescriptionCharCounter.textContent = `${remaining} characters remaining`;

      if (remaining < 50) {
        visitDescriptionCharCounter.classList.add('char-counter-red');
      } else {
        visitDescriptionCharCounter.classList.remove('char-counter-red');
      }
    };

    // Initial update
    updateCharCounter();

    // Update on input
    visitDescriptionTextarea.addEventListener('input', updateCharCounter);
  }

})(jQuery);
