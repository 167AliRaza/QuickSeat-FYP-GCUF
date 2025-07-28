# QuickSeat with Ai Powered customer Support Assistant 

QuickSeat is a user-friendly application designed to manage bus seating arrangements, primarily for university students. It allows users to register, log in, view bus schedules, and administrators to add or delete buses from the system. The application aims to streamline the process of booking and managing bus seats efficiently.

## Table of Contents

- Features
- Technologies Used
- Installation
- Usage
- Contributing
- License

## Features

- **User Registration and Login**: Users can sign up with their email, password, and university roll number, and agree to the Terms of Use and Privacy Policy.
- **Bus Schedule Management**: View a detailed bus schedule with availability and booking status for up to 70 buses.
- **Admin Functions**: Add new buses or delete existing ones using a bus number.
- **Responsive Interface**: Designed to work seamlessly on both web and mobile devices.

## Technologies Used

- **Frontend**: xml
- **Backend**: Kotlin
- **Database**: FireBase

## Installation

To set up the QuickSeat project locally, follow these steps:

1. **Clone the Repository**:

   ```bash
   git clone https://github.com/your-username/quickseat.git
   cd quickseat
   ```

2. **Install Dependencies**: (Specify dependencies, e.g., for a Node.js project)

   ```bash
   npm install
   ```

3. **Set Up Environment Variables**: Create a `.env` file in the root directory and add necessary configurations (e.g., database credentials, API keys).


## Usage
QuickSeat provides a streamlined interface for managing bus seating. Below are detailed usage scenarios for different user types:

1. **User Registration**:
   - Navigate to the sign-up page.
   - Enter your email, password, confirm password, and university roll number.
   - Check the box to agree to QuickSeat's Terms of Use and Privacy Policy.
   - Click "Sign up" to create your account.
   - Upon successful registration, you’ll be redirected to the login page.

2. **User Login**:
   - Go to the login page.
   - Enter your registered email and password.
   - Click "Sign in" to access your account.
   - If you’ve forgotten your password, click "Forgot Password?" to initiate the password reset process.

3. **Viewing Bus Schedules**:
   - After logging in, navigate to the bus schedule section.
   - View a table displaying available buses, including details like timing (e.g., 15:30), date (e.g., 20/5/2025), and available seats (e.g., 70 or 40 seats).
   - Seats are color-coded: green for available and red for booked.
   - Select a bus to view detailed seating arrangements or to book a seat.

4. **Booking a Seat**:
   - From the bus schedule, click on a bus to see its seat layout.
   - Choose an available seat (marked green) and confirm your booking.
   - After booking, the seat will turn red, indicating it’s reserved.
   - Receive a confirmation email with your booking details (if configured).

5. **Admin: Adding a New Bus**:
   - Log in with an admin account.
   - Navigate to the admin panel and select "Add New Bus."
   - Enter bus details such as bus number, schedule (e.g., timing: 15:15, date: 20/5/2025), and total seats (e.g., 70).
   - Submit to add the bus to the schedule.

6. **Admin: Deleting a Bus**:
   - In the admin panel, go to the "Delete Bus" section.
   - Enter the bus number you wish to remove (e.g., Bus #45).
   - Confirm deletion to remove the bus from the system.

7. **Admin: Resetting Ratings**:
   - Access the "Reset Ratings" feature in the admin panel.
   - Use this to clear user ratings or feedback for buses, if applicable (e.g., for a rating system tied to bus services).
   - Confirm the reset action to update the database.


## Contributing

Contributions are welcome! To contribute:

1. Fork the repository.
2. Create a new branch (`git checkout -b feature/your-feature`).
3. Commit your changes (`git commit -m 'Add your feature'`).
4. Push to the branch (`git push origin feature/your-feature`).
5. Open a pull request.

Please ensure your code follows the project's coding standards and includes appropriate tests.

## License

### This project is licensed under the MIT License. See the LICENSE file for details.
