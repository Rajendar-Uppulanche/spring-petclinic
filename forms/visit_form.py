from flask_wtf import FlaskForm
from wtforms import DateField, StringField, SubmitField, SelectField
from wtforms.validators import DataRequired, Length
from datetime import date


class VisitForm(FlaskForm):
    date = DateField("Date", validators=[DataRequired()], format='%Y-%m-%d')
    description = StringField("Description", validators=[DataRequired(), Length(min=1, max=200)])
    veterinarian = SelectField("Veterinarian", coerce=int, validators=[DataRequired()])
    submit = SubmitField("Add Visit")

    def validate_date(self, field):
        if field.data > date.today():
            raise ValidationError("Date cannot be in the future.")
