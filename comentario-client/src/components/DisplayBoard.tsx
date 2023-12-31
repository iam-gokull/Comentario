import api from "../api/apiConfig";
import { Board, UserState } from "../interfaces/types"
import { Link, useNavigate } from "react-router-dom"

interface Props {
    board: Board
    isYourBoard: boolean;
    isYourDashboard: boolean;
    handleEditModal: (board: Board) => void | undefined;
    loggedInUser: UserState | undefined;
}

const DisplayBoard: React.FC<Props> = ({ board, isYourBoard, handleEditModal, isYourDashboard, loggedInUser }) => {

    const navigate = useNavigate();

    if (!board) return;

    const IsEditModal = () => {
        handleEditModal(board);
    }

    const handleUrlClick = () => {
        if (!isYourBoard && !isYourDashboard && loggedInUser?.username !== board.username) {
            api.put(`/boards/update/url-click/${board.id}`, null)
            .then((response) => console.log(response.data))
            .catch((error) => console.error(error))
        }
    }

    return (
        <div className="bg-primaryWhite w-full p-6 rounded-md shadow flex flex-col justify-between space-y-3">
            <div>
                <Link onClick={handleUrlClick} target="_blank" rel="noopener noreferrer" to={!isYourBoard && !isYourDashboard ? board.url : `/board/${board.id}`}>
                    <img
                        src={board.imageData.imageUrl}
                        alt="image"
                        className="rounded-md border border-solid border-black object-center object-cover h-44 w-full"
                    />
                </Link>
                <div className="flex flex-col justify-between mt-4">
                    <div className="flex justify-between items-center">
                        <div className="flex gap-1 items-center">
                            <Link onClick={handleUrlClick} target="_blank" rel="noopener noreferrer" to={!isYourBoard && !isYourDashboard ? board.url : `/board/${board.id}`} className="text-black font-bold py-2">{board.title}</Link>
                            {board.self &&
                                <img
                                    src="../src/assets/self.png"
                                    alt="feedback icon"
                                    className=" h-4"
                                />}
                        </div>

                        {!isYourBoard && !isYourDashboard && <div className="flex gap-1 items-end py-2">
                            <img
                                src="../src/assets/authored-by-icon.png"
                                alt="feedback icon"
                                className=" h-5"
                            />
                             <h3 className="text-sm font-small font-bold text-gray-500">{board.username}</h3>
                        </div>}

                    </div>
                    <p className="text-black break-words">{board.description}</p>
                </div>
            </div>
            {isYourBoard && <div>
                <button className="text-sm font-small font-bold text-white bg-primaryBlue rounded-md p-2 hover:brightness-125" onClick={IsEditModal}>Edit board</button>
                </div>}
            {!isYourBoard && !isYourDashboard && <div className="flex justify-between">
                <button className="text-sm font-small font-bold text-white bg-primaryBlue rounded-md p-2 hover:brightness-125" onClick={() => navigate(`/board/${board.id}`)}>Provide feedback</button>
                <div className="flex gap-2 items-center">
                    <img
                        src="../src/assets/feedback-icon.png"
                        alt="feedback icon"
                        className="logo-image h-6"
                    />
                    <span className="text-black">{board.feedbacks === null ? 0 : board.feedbacks.length}</span>
                </div>
            </div>}
            {isYourDashboard && <div className="flex justify-between">
            <button className="text-sm font-small font-bold text-white bg-primaryBlue rounded-md p-2 hover:brightness-125" onClick={() => navigate(`/my-board/${board.id}`)}>View analysis</button>
                <div className="flex gap-2 items-center">
                    <img
                        src="../src/assets/feedback-icon.png"
                        alt="feedback icon"
                        className="logo-image h-6"
                    />
                    <span className="text-black">{board.feedbacks === null ? 0 : board.feedbacks.length}</span>
                </div>
            </div>}
        </div>
    )
}

export default DisplayBoard