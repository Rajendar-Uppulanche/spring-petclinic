class Visit < ApplicationRecord
  belongs_to :pet
  belongs_to :veterinarian # Assuming this relationship

  validates :visit_date, presence: true
  validates :reason, presence: true, length: { minimum: 5 }
  validates :weight, numericality: { greater_than: 0, allow_nil: true, message: "must be a positive number" }

  # Other existing validations/associations
end