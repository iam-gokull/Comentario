import React, { useEffect, useState } from "react"
import { Feedback } from "../interfaces/types"
import { Link } from "react-router-dom";
import api from "../api/apiConfig";

interface Props {
    feedback: Feedback | undefined;
}


const DisplayFeedback: React.FC<Props> = ({ feedback }) => {
    const [upVote, setUpVote] = useState(false);
    const [upVoteCount, setUpVoteCount] = useState(0);

    useEffect(() => {
        const token = localStorage.getItem('jwt');
        const config = {
            headers: {
                Authorization: token,
            }
        };
        api.get(`/feedbacks/upvote/${feedback?.id}`, config)
            .then(response => {
                console.log(response.data)
                const propertyNames = Object.keys(response.data);
                const key = propertyNames[0];
                const value = response.data[key];
                if (key==="true") {
                    setUpVote(true);
                } else {
                    setUpVote(false);
                }
                setUpVoteCount(value);
            })
            .catch(error => {
                console.error(error);
            })

    }, [upVote, upVoteCount, feedback?.id])


    const handleUpVote = () => {
        const token = localStorage.getItem('jwt');
        if (token) {

            const config = {
                headers: {
                    'Authorization': token,
                }
            };
            if (upVote) {
                api
                    .delete(`/feedbacks/upvote/delete/${feedback?.id}`, config)
                    .then(response => {
                        console.log('Delete Request:', response.config);
                        console.log('Delete Response:', response.data);
                        setUpVote(false);
                        setUpVoteCount(response.data.upVoteCount);
                    })
                    .catch(error => {
                        console.error('Delete Error:', error);
                    });
            } else {
                api
                    .put(`/feedbacks/upvote/add/${feedback?.id}`, null, config)
                    .then(response => {
                        console.log('Add Request:', response.config);
                        console.log('Add Response:', response.data);
                        setUpVote(true);
                        setUpVoteCount(response.data.upVoteCount);
                    })
                    .catch(error => {
                        console.error('Add Error:', error);
                    });
            }
        } else {
            console.error('Token is null or empty');
        }
    }

    return (
        <div className='container flex flex-row bg-primaryWhite rounded-md p-8 justify-between'>
            <div className="flex md:gap-8">
                <div className="w-16 md:w-auto">
                    <img src={feedback?.profileUrl}
                        alt="profile Image"
                        className="h-12 w-12 md:h-16 md:w-16 object-center object-cover rounded-full">
                    </img>
                </div>
                <div>
                    <h4 className="text-primaryBlue font-bold text-sm">@{feedback?.username}</h4>
                    <div className="mt-1">
                        <div className="flex items-center gap-2">
                            <Link to={`/feedback/${feedback?.id}`}>
                                <h1 className="text-black font-bold">{feedback?.title}</h1>
                            </Link>

                            <span className="bg-bgColor rounded-xl text-primaryBlue p-2 text-sm shadow-xl font-bold">{feedback?.category}</span>
                        </div>

                        <p className="text-black text-sm">{feedback?.description}</p>
                    </div>

                    <div className="pt-4 flex items-end gap-1">
                        <img src={`${upVote ? "../src/assets/up-arrow-filled.png" : "../src/assets/up-arrow.png"} `}
                            alt="logo"
                            className="h-6 cursor-pointer"
                            onClick={handleUpVote}>
                        </img>
                        <p className={`${upVote ? "text-primaryBlue" : "text-black"}  font-bold`}>{upVoteCount}</p>
                    </div>
                </div>
            </div>
            <div className="flex items-center gap-1">
                <img src="../src/assets/bubble-chat.png"
                    alt="logo"
                    className="h-6">
                </img>
                <p className="text-black font-bold">{feedback?.comments?.length}</p>
            </div>
        </div>
    )
}

export default DisplayFeedback