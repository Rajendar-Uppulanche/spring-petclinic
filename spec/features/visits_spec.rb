require 'rails_helper'

RSpec.feature "Visits management", type: :feature do
  let!(:pet) { create(:pet, name: "Buddy") }
  let!(:veterinarian) { create(:veterinarian, name: "Dr. Smith") }

  before do
    # Assuming a user is logged in for feature tests
    # If authentication is present, add `login_as create(:user)` or similar
  end

  scenario "User creates a new visit with pet weight" do
    visit new_visit_path

    select pet.name, from: "visit_pet_id"
    select veterinarian.name, from: "visit_veterinarian_id"
    fill_in "Visit date", with: Date.today.to_s
    fill_in "Reason", with: "Annual checkup and vaccinations"
    fill_in "Pet Weight (kg)", with: "12.3" # New field

    click_button "Create Visit"

    expect(page).to have_text("Visit was successfully created.")
    expect(page).to have_text("Pet Weight (kg): 12.3") # Verify display
    expect(Visit.last.weight).to eq(12.3)
  end

  scenario "User updates an existing visit to include pet weight" do
    visit_record = create(:visit, pet: pet, veterinarian: veterinarian, reason: "Initial visit")
    visit edit_visit_path(visit_record)

    fill_in "Pet Weight (kg)", with: "13.5" # New field
    click_button "Update Visit"

    expect(page).to have_text("Visit was successfully updated.")
    expect(page).to have_text("Pet Weight (kg): 13.5")
    expect(visit_record.reload.weight).to eq(13.5)
  end

  scenario "User tries to create a visit with invalid pet weight" do
    visit new_visit_path

    select pet.name, from: "visit_pet_id"
    select veterinarian.name, from: "visit_veterinarian_id"
    fill_in "Visit date", with: Date.today.to_s
    fill_in "Reason", with: "Annual checkup"
    fill_in "Pet Weight (kg)", with: "-5.0" # Invalid weight

    click_button "Create Visit"

    expect(page).to have_text("prohibited this visit from being saved")
    expect(page).to have_text("Weight must be a positive number")
    expect(Visit.count).to eq(0) # No visit should be created
  end

  # Other existing feature tests
end