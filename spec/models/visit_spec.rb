require 'rails_helper'

RSpec.describe Visit, type: :model do
  let(:pet) { create(:pet) } # Assuming FactoryBot
  let(:veterinarian) { create(:veterinarian) }

  it "is valid with valid attributes" do
    visit = Visit.new(pet: pet, veterinarian: veterinarian, visit_date: Date.today, reason: "Routine checkup")
    expect(visit).to be_valid
  end

  it "is not valid without a visit_date" do
    visit = Visit.new(pet: pet, veterinarian: veterinarian, reason: "Routine checkup")
    expect(visit).to_not be_valid
    expect(visit.errors[:visit_date]).to include("can't be blank")
  end

  it "is not valid without a reason" do
    visit = Visit.new(pet: pet, veterinarian: veterinarian, visit_date: Date.today)
    expect(visit).to_not be_valid
    expect(visit.errors[:reason]).to include("is too short (minimum is 5 characters)")
  end

  describe "weight validation" do
    it "is valid with a positive weight" do
      visit = Visit.new(pet: pet, veterinarian: veterinarian, visit_date: Date.today, reason: "Routine checkup", weight: 10.5)
      expect(visit).to be_valid
    end

    it "is valid with a nil weight" do
      visit = Visit.new(pet: pet, veterinarian: veterinarian, visit_date: Date.today, reason: "Routine checkup", weight: nil)
      expect(visit).to be_valid
    end

    it "is not valid with a zero weight" do
      visit = Visit.new(pet: pet, veterinarian: veterinarian, visit_date: Date.today, reason: "Routine checkup", weight: 0)
      expect(visit).to_not be_valid
      expect(visit.errors[:weight]).to include("must be a positive number")
    end

    it "is not valid with a negative weight" do
      visit = Visit.new(pet: pet, veterinarian: veterinarian, visit_date: Date.today, reason: "Routine checkup", weight: -5.0)
      expect(visit).to_not be_valid
      expect(visit.errors[:weight]).to include("must be a positive number")
    end

    it "is not valid with non-numeric weight" do
      visit = Visit.new(pet: pet, veterinarian: veterinarian, visit_date: Date.today, reason: "Routine checkup", weight: "abc")
      expect(visit).to_not be_valid
      expect(visit.errors[:weight]).to include("must be a positive number") # Rails converts non-numeric to 0 for numericality validation
    end
  end
end